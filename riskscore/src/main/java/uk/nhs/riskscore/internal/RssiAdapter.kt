package uk.nhs.riskscore.internal

import uk.nhs.riskscore.ObservationType
import uk.nhs.riskscore.PowerLossParameters
import kotlin.math.ln

internal class RssiAdapter(
    powerLossParameters: PowerLossParameters,
    val weightCoefficient: Double,
    val intercept: Double,
    val observationType: ObservationType
    ) {

    private val friisCalculator = FriisCalculator(powerLossParameters)

    fun observationTransform(distance: Double): Double {
        val friis = friisCalculator.friis(0.0, 0.0)
        val transform: (Double) -> Double = when(observationType) {
            ObservationType.log -> ::ln
            ObservationType.gen -> { d -> ln(-1.0 * friis(d)) }
        }
        return weightCoefficient * transform(distance) + intercept
    }
}