package uk.nhs.riskscore.internal

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.checkAll
import uk.nhs.riskscore.internal.kotest.shouldBeCloseTo

internal class GammaDistributionTest: StringSpec({
    "test precomputed values of inverseCDF" {
        GammaDistribution(2.5, 1.0).inverseCDF(1.199675720590626e-02) shouldBeCloseTo 0.3
        GammaDistribution(10.0, 1.0).inverseCDF(1.114254783387200e-07) shouldBeCloseTo 1.0
        GammaDistribution(0.5, 1.0).inverseCDF(0.0) shouldBeCloseTo 0.0
    }

    "test median" {
        GammaDistribution.median(2.5, 1.0) shouldBeCloseTo 2.175730095547763
    }

    "scale parameter multiplies the inverseCDF result" {
        checkAll(100,
            Arb.numericDoubles(0.0, 2.0).filter { it > 0.0 },
            Arb.numericDoubles(0.5, 2.0).filter { it > 0.0 },
            Arb.numericDoubles(0.0, 1.0).filter { it > 0.0 }.filter { it < 1.0 }
        ) { shape, scale, probability ->
            val scaled = GammaDistribution(shape, scale).inverseCDF(probability)
            val unscaled = GammaDistribution(shape,1.0).inverseCDF(probability)
            scaled shouldBeCloseTo scale * unscaled
        }
    }

    "scale parameter multiplies the median result" {
        checkAll(100,
            Arb.numericDoubles(0.0, 2.0).filter { it > 0.0 },
            Arb.numericDoubles(0.5, 2.0).filter { it > 0.0 }
        ) { shape, scale ->
            val scaled = GammaDistribution.median(shape, scale)
            val unscaled = GammaDistribution.median(shape,1.0)
            scaled shouldBeCloseTo scale * unscaled
        }
    }
})