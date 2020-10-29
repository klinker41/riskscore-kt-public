package uk.nhs.riskscore.internal

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.property.checkAll
import uk.nhs.riskscore.internal.kotest.nonEmptyList
import uk.nhs.riskscore.internal.kotest.smallPositiveDoubles

internal class QuadraticRiskScoreTest : StringSpec({
    "risk score is bounded by sum of durations" {
        checkAll(
            smallPositiveDoubles,
            smallPositiveDoubles.nonEmptyList(),
            smallPositiveDoubles.nonEmptyList()
        ) { minDistance, durations, distances ->
            val score = QuadraticRiskScore(minDistance).calculate(
                durations = durations,
                distances = distances,
                shouldNormalize = false
            )
            score shouldBeLessThanOrEqual durations.sum()
        }
    }

    "normalized risk score is bounded above by 1" {
        checkAll(
            smallPositiveDoubles,
            smallPositiveDoubles.nonEmptyList(),
            smallPositiveDoubles.nonEmptyList()
        ) { minDistance, durations, distances ->
            val score = QuadraticRiskScore(minDistance).calculate(
                durations = durations,
                distances = distances,
                shouldNormalize = true
            )
            score shouldBeLessThanOrEqual 1.0
        }
    }
})
