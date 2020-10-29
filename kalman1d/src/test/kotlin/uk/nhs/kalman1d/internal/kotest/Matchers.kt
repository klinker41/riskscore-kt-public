package uk.nhs.kalman1d.internal.kotest

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import uk.nhs.kalman1d.internal.State

internal infix fun Double.shouldBeCloseTo(expected: Double) {
    shouldBe(expected plusOrMinus 1e-8)
}

internal fun State.shouldMatch(expectedMean: Double, expectedCovariance: Double) {
    mean.rawValue shouldBeCloseTo expectedMean
    covariance.rawValue shouldBeCloseTo expectedCovariance
}