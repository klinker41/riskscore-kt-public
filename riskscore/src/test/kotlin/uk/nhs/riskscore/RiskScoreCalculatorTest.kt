package uk.nhs.riskscore

import io.kotest.core.spec.style.StringSpec
import uk.nhs.riskscore.internal.kotest.shouldBeCloseTo

internal class RiskScoreCalculatorTest : StringSpec({
    "calculate risk scaore with example configuration" {
        val calculator = RiskScoreCalculator(RiskScoreCalculatorConfiguration.exampleConfiguration)
        calculator.calculate(exampleDataSingleValues) shouldBeCloseTo  2.1528634510939413
    }
})
