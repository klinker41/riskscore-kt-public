package uk.nhs.riskscorechecker

import com.google.protobuf.util.JsonFormat
import uk.nhs.riskscore.RiskScoreCalculator
import uk.nhs.riskscore.RiskScoreCalculatorConfiguration.Companion.exampleConfiguration
import uk.nhs.riskscore.ScanInstance
import uk.nhs.support.InstanceName
import uk.nhs.support.RiskScoreValue
import java.io.File
import com.google.en.riskscore.Experiment

fun main() {
    computeScores(parseJsonFiles()).forEach { (instance, riskScore) ->
        println("${instance.name}: ${riskScore.score}")
    }
}

fun parseJsonFiles(): Sequence<Pair<InstanceName, List<ScanInstance>>> {
    return getResourceFile("/test_results").walk()
        .filter { file -> file.name.endsWith(".json") }
        .flatMap { file -> parseScanInstances(file) }
}

fun getResourceFile(path: String): File {
    return File(object {}.javaClass.getResource(path).file)
}

fun parseScanInstances(file: File): Sequence<Pair<InstanceName, List<ScanInstance>>> {
    var experiment = parseExperiment(file)
    return sequence {
        experiment.getParticipantsList().forEach { participant ->
            participant.getResultsList().forEach { result ->
                result.getCounterpartsList().forEach { counterpart ->
                    if (counterpart.getExposureWindowsCount() > 0) {
                        // TODO(jklinker): Are we supposed to combine all scan instances from all
                        //  exposure windows for a counterpart, or process some other way?
                        var scans = mutableListOf<ScanInstance>()
                        counterpart.getExposureWindowsList().forEach { exposureWindow ->
                            exposureWindow.getScanInstancesList().forEach { scanInstance ->
                                scans.add(
                                    // TODO(jklinker): Do scan instances take typical attenuation
                                    // or min attenuation?
                                    ScanInstance(
                                        scanInstance.getTypicalAttenuationDb(),
                                        scanInstance.getSecondsSinceLastScan()
                                    )
                                )
                            }
                        }
                        yield(
                            Pair(
                                InstanceName(
                                    "${file.name.removeSuffix(".json")}: " +
                                            "${participant.getDeviceName()} -> " +
                                            "${counterpart.getDeviceName()}"),
                                scans.toList()
                            )
                        )
                    }
                }
            }
        }
    }
}

fun parseExperiment(file: File): Experiment {
    var experiment = Experiment.newBuilder()
    JsonFormat.parser().merge(file.readText(), experiment)
    return experiment.build()
}

fun computeScores(
    scans: Sequence<Pair<InstanceName, List<ScanInstance>>>
): Sequence<Pair<InstanceName, RiskScoreValue>> {
    val calculator = RiskScoreCalculator(exampleConfiguration)
    return scans.map { (name, scans) ->
        val score = calculator.calculate(scans)
        Pair(name, RiskScoreValue(score))
    }
}

