package uk.nhs.riskscorechecker

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import uk.nhs.riskscore.RiskScoreCalculator
import uk.nhs.riskscore.RiskScoreCalculatorConfiguration.Companion.exampleConfiguration
import uk.nhs.riskscorechecker.NumberToCompute.All
import uk.nhs.riskscorechecker.NumberToCompute.Number
import uk.nhs.support.InstanceName
import uk.nhs.support.RiskScoreResultsCSVDecoder
import uk.nhs.support.RiskScoreValue
import uk.nhs.support.ScanInstanceCSVDecoder
import java.io.File

fun main(args: Array<String>) {
    val parser = ArgParser("risk-score-checker")

    val scanInstanceDir by parser.argument(ArgType.String, description = "The path to the scaninstance directory")
    val riskScorePath by parser.argument(ArgType.String, description = "The path to the riskscore file")
    val number by parser.option(NumberToComputeChoice, shortName = "n", description = "The number of scaninstances to calculate")
        .default(All)
    parser.parse(args)

    val expectedResults = RiskScoreResultsCSVDecoder.decode(riskScorePath)

    println("experiment python kotlin")
    computeScores(number, scanInstanceDir).forEach { (instance, riskscore) ->
        val expectedScore = checkNotNull(expectedResults.get(instance)) { "Cannot find result for ${instance.name}" }
        println("${instance.name} ${expectedScore.score} ${riskscore.score}")
    }
}

fun computeScores(number: NumberToCompute, scanInstanceDir: String): Sequence<Pair<InstanceName, RiskScoreValue>> {
    val calculator = RiskScoreCalculator(exampleConfiguration)

    val maximum = when (number) {
        is All -> Int.MAX_VALUE
        is Number -> number.maximum
    }

    return File(scanInstanceDir).walk().maxDepth(1).filter(File::isFile).take(maximum).map { file ->
        val instances = ScanInstanceCSVDecoder.decode(file.path)
        val score = calculator.calculate(instances)
        Pair(InstanceName(file.name), RiskScoreValue(score))
    }
}

