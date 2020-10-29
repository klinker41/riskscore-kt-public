package uk.nhs.riskscore

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.paths.*
import uk.nhs.riskscore.RiskScoreCalculatorConfiguration.Companion.exampleConfiguration
import uk.nhs.support.InstanceName
import uk.nhs.support.RiskScoreResultsCSVDecoder
import uk.nhs.support.ScanInstanceCSVDecoder
import java.nio.file.Paths
import kotlin.math.abs

internal class ScenarioTest: StringSpec({
    "computed riskscores are within 5% of the python computed riskscores" {
        val rootPath = Paths.get("src","test","resources", "TestData").toAbsolutePath()
        val expectedRiskScoresPath = rootPath.resolve("pythonRiskScores.csv")
        expectedRiskScoresPath.shouldBeAFile()
        val scanInstanceDirectoryPath = Paths.get(rootPath.toString(), "ScanInstances")
        scanInstanceDirectoryPath.shouldBeADirectory()
        val calculator = RiskScoreCalculator(exampleConfiguration)

        val expectedScores = RiskScoreResultsCSVDecoder.decode(expectedRiskScoresPath.toFile().path)

        scanInstanceDirectoryPath.toFile().listFiles()!!.forEach { scanInstanceFile ->
            val instances = ScanInstanceCSVDecoder.decode(scanInstanceFile.path)

            val actualScore = calculator.calculate(instances)
            val expectedScore = expectedScores[InstanceName(scanInstanceFile.name)]!!.score

            abs(expectedScore - actualScore) shouldBeLessThan (actualScore * 0.05)
        }
    }
})