package uk.nhs.riskscore.internal.kotest

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

internal infix fun Double.shouldBeCloseTo(expected: Double) {
    shouldBe(expected plusOrMinus 1e-8)
}