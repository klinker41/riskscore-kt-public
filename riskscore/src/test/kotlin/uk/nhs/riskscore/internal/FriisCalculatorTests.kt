package uk.nhs.riskscore.internal

import io.kotest.core.spec.style.StringSpec
import uk.nhs.riskscore.PowerLossParameters
import uk.nhs.riskscore.internal.kotest.shouldBeCloseTo
import kotlin.math.PI
import kotlin.math.pow

internal class FriisCalculatorTests: StringSpec() {
    init {
        "Friis with norm wavelength unit loss" {
            val params = PowerLossParameters(
                wavelength = 4.0 * PI,
                pathLossFactor = 1.0,
                refDeviceLoss = 0.0
            )
            assertFriisAndInverse(
                distance = 10.0.pow(2.0),
                rssi = -2.0,
                rxCorrection = 0.0,
                txCorrection = 0.0,
                params = params
            )
        }

        "Friis with zero rssi and unit wavelength" {
            val params = PowerLossParameters(
                wavelength = 1.0,
                pathLossFactor = 1.0,
                refDeviceLoss = 0.0
            )

            val fc = FriisCalculator(params)
            val distance = 1/(4.0 * PI)
            val calculatedRssi = fc.friis(0.0, 0.0)(distance)
            val calculatedDistance = fc.inverseFriis(0.0, 0.0)(0.0)

            // minimum rssi estimate is -1e-3
            -1e-3 shouldBeCloseTo calculatedRssi
            distance shouldBeCloseTo calculatedDistance
        }

        "Friis with double path loss factor" {
            val params = PowerLossParameters(
                wavelength = 4.0 * PI,
                pathLossFactor = 2.0,
                refDeviceLoss = 0.0
            )

            val fc = FriisCalculator(params)
            val distance = 0.1
            val calculatedRssi = fc.friis(0.0, 0.0)(distance)
            val calculatedDistance = fc.inverseFriis(0.0, 0.0)(2.0)

            // minimum rssi estimate is -1e-3
            -1e-3 shouldBeCloseTo calculatedRssi
            distance shouldBeCloseTo calculatedDistance
        }

        "Friis with rxCorrection" {
            val params = PowerLossParameters(
                wavelength = 4.0 * PI,
                pathLossFactor = 1.0,
                refDeviceLoss = 0.0
            )
            assertFriisAndInverse(
                distance = 10.0.pow(3.0),
                rssi = -2.0,
                rxCorrection = 1.0,
                txCorrection = 0.0,
                params = params
            )
        }

        "Friis with txCorrection" {
            val params = PowerLossParameters(
                wavelength = 4.0 * PI,
                pathLossFactor = 1.0,
                refDeviceLoss = 0.0
            )
            assertFriisAndInverse(
                distance = 10.0.pow(3.0),
                rssi = -2.0,
                rxCorrection = 0.0,
                txCorrection = 1.0,
                params = params
            )
        }

        "Friis with device loss" {
            val params = PowerLossParameters(
                wavelength = 4.0 * PI,
                pathLossFactor = 1.0,
                refDeviceLoss = 1.0
            )
            assertFriisAndInverse(
                distance = 10.0.pow(3.0),
                rssi = -2.0,
                rxCorrection = 0.0,
                txCorrection = 0.0,
                params = params
            )
        }
    }

    fun assertFriisAndInverse(
        distance: Double,
        rssi: Double,
        rxCorrection: Double,
        txCorrection: Double,
        params: PowerLossParameters
    ) {
        val fc = FriisCalculator(params)
        val calculatedRssi = fc.friis(rxCorrection, txCorrection)(distance)
        val calculatedDistance = fc.inverseFriis(rxCorrection, txCorrection)(rssi)

        rssi shouldBeCloseTo calculatedRssi
        distance shouldBeCloseTo calculatedDistance
    }
}