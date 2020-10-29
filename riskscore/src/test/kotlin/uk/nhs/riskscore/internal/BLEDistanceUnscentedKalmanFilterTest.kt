package uk.nhs.riskscore.internal

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.doubles.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import uk.nhs.riskscore.internal.kotest.bleDistanceUnscentedKalmanFilter
import uk.nhs.riskscore.internal.kotest.nonEmptyList
import uk.nhs.riskscore.internal.kotest.smallNumericDoubles
import uk.nhs.riskscore.internal.kotest.smallPositiveDoubles

internal class BLEDistanceUnscentedKalmanFilterTest : StringSpec({
    "transition function is non-negative" {
        checkAll(
            smallNumericDoubles,
            smallNumericDoubles,
            bleDistanceUnscentedKalmanFilter) { state, noise, filter ->
            val result = filter.transitionFunction(state, noise)
            result shouldBeGreaterThanOrEqual 0.0
        }
    }

    "smooth function returns the same number of elements as the input" {
        checkAll(
            100,
            bleDistanceUnscentedKalmanFilter,
            smallPositiveDoubles.orNull(0.1).nonEmptyList(2)
        ) { filter, attenutations ->
            filter.smooth(attenutations).size shouldBeExactly attenutations.size
        }
    }

    "smooth function returns finite results" {
        checkAll(
            100,
            bleDistanceUnscentedKalmanFilter,
            smallPositiveDoubles.orNull(0.1).nonEmptyList(2)
        ) { filter, attenutations ->
            filter.smooth(attenutations).all { (mean, covariance) ->
                mean.isFinite() && covariance.isFinite()
            }.shouldBeTrue()
        }
    }
})
