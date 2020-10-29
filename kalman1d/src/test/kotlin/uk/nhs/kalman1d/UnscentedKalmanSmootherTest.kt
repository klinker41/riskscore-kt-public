package uk.nhs.kalman1d

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import uk.nhs.kalman1d.internal.kotest.shouldBeCloseTo
import kotlin.math.abs
import kotlin.math.ln

internal class UnscentedKalmanSmootherTest : StringSpec() {
    companion object {
        fun pythonSmoother(listener: FilterListener = nullFilterListener) = UnscentedKalmanSmoother(
            2.0,
            10.0,
            0.01,
            0.49476144,
            1.0,
            0.0,
            0.0,
            { s, n -> abs(s + n) },
            { s, n -> 0.1270547531082051 * ln(abs(s)) + 4.2309333657856945 + n },
            listener
        )

        val input = listOf(
            null,
            4.17411483628451,
            4.4031070780507395,
            4.328546097488628,
            4.2601062473100395,
            4.220570477970699,
            4.16624909786391,
            4.201804139521874,
            4.279699043501633,
            4.265813830062009,
            4.17482861142975,
            4.240065076412635,
            4.272051055120574,
            4.15668386886749,
            4.146032357856017
        )

        val expectedFilteredMean = listOf(
            2.0,
            3.11281975,
            3.25116568,
            3.25039652,
            3.21769892,
            3.16948161,
            3.10003209,
            3.04646132,
            3.02735491,
            3.00281025,
            2.94367877,
            2.91109234,
            2.89179044,
            2.83036511,
            2.76550021
        )

        val expectedFilteredCovariance = listOf(
            10.0,
            3.9988494,
            3.1987703,
            3.09336277,
            3.00792226,
            2.92810349,
            2.84944893,
            2.7664405,
            2.68386272,
            2.61095034,
            2.54398901,
            2.47451309,
            2.40897963,
            2.34940361,
            2.28631914
        )

        val expectedSmoothedMean = listOf(
            1.50730615,
            2.61527532,
            2.77742298,
            2.77594196,
            2.77440818,
            2.77293444,
            2.77158016,
            2.77042747,
            2.76942968,
            2.76846866,
            2.76757112,
            2.76687887,
            2.76629608,
            2.76577513,
            2.76550021
        )

        val expectedSmoothedCovariance = listOf(
            8.92273898,
            2.71790844,
            2.2085437,
            2.21234272,
            2.21663732,
            2.22136724,
            2.22653173,
            2.23215186,
            2.23828224,
            2.24495561,
            2.25214671,
            2.25984783,
            2.26810931,
            2.27693734,
            2.28631914
        )
    }

    init {
        "filter results agree with Python results" {
            val meanIterator = expectedFilteredMean.listIterator()
            val covarianceIterator = expectedFilteredCovariance.listIterator()
            val smoother = pythonSmoother(object : FilterListener {
                override fun states(index: Int, mean: Double, covariance: Double) {
                    mean shouldBeCloseTo meanIterator.next()
                    covariance shouldBeCloseTo covarianceIterator.next()
                }
            })
            smoother.smooth(input)
        }

        "smoother results agree with Python results" {
            val smoothed = pythonSmoother().smooth(input)

            smoothed shouldHaveAtLeastSize 1
            expectedSmoothedMean.zip(expectedSmoothedCovariance).zip(smoothed).forEach { (expected, actual) ->
                val (expectedMean, expectedCovariance) = expected
                val (actualMean, actualCovariance) = actual

                expectedMean shouldBeCloseTo actualMean
                expectedCovariance shouldBeCloseTo actualCovariance
            }
        }
    }
}
