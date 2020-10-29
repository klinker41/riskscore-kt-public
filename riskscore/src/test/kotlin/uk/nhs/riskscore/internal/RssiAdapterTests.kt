package uk.nhs.riskscore.internal

import io.kotest.core.spec.style.StringSpec
import uk.nhs.riskscore.ObservationType
import uk.nhs.riskscore.PowerLossParameters
import uk.nhs.riskscore.internal.kotest.shouldBeCloseTo
import kotlin.math.E
import kotlin.math.PI

internal val fourPi = 4.0 * PI

internal class RssiAdapterTests: StringSpec({
    "observationTransform is log for log type" {
        val params = PowerLossParameters(
            wavelength = fourPi,
            pathLossFactor = 1.0,
            refDeviceLoss = 1.0
        )

        val ra = RssiAdapter(
            params,
            weightCoefficient = 1.0,
            intercept = 0.0,
            observationType = ObservationType.log
        )

        ra.observationTransform(E) shouldBeCloseTo 1.0
    }

    "observationTransform is log for log type weight coefficient" {
        val params = PowerLossParameters(
            wavelength = fourPi,
            pathLossFactor = 1.0,
            refDeviceLoss = 1.0
        )

        val ra = RssiAdapter(
            params,
            weightCoefficient = 2.0,
            intercept = 0.0,
            observationType = ObservationType.log
        )

        ra.observationTransform(E) shouldBeCloseTo 2.0
    }

    "observationTransform is log for log type intercept" {
        val params = PowerLossParameters(
            wavelength = fourPi,
            pathLossFactor = 1.0,
            refDeviceLoss = 1.0
        )

        val ra = RssiAdapter(
            params,
            weightCoefficient = 2.0,
            intercept = 1.0,
            observationType = ObservationType.log
        )

        ra.observationTransform(E) shouldBeCloseTo 3.0
    }

    "observationTransform is friis for gen type" {
        val params = PowerLossParameters(
            wavelength = fourPi,
            pathLossFactor = 1.0,
            refDeviceLoss = 3.0 - E
        )

        val ra = RssiAdapter(
            params,
            weightCoefficient = 1.0,
            intercept = 0.0,
            observationType = ObservationType.gen
        )

        ra.observationTransform(1e3) shouldBeCloseTo 1.0
    }

    "observationTransform is friis for gen type weightCoefficient" {
        val params = PowerLossParameters(
            wavelength = fourPi,
            pathLossFactor = 1.0,
            refDeviceLoss = 3.0 - E
        )

        val ra = RssiAdapter(
            params,
            weightCoefficient = 0.5,
            intercept = 0.0,
            observationType = ObservationType.gen
        )

        ra.observationTransform(1e3) shouldBeCloseTo 0.5
    }

    "observationTransform is friis for gen type intercept" {
        val params = PowerLossParameters(
            wavelength = fourPi,
            pathLossFactor = 1.0,
            refDeviceLoss = 3.0 - E
        )

        val ra = RssiAdapter(
            params,
            weightCoefficient = 1.0,
            intercept = -2.0,
            observationType = ObservationType.gen
        )

        ra.observationTransform(1e3) shouldBeCloseTo -1.0
    }
})