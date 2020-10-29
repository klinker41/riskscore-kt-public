package uk.nhs.riskscore.internal.kotest

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.*
import io.kotest.property.exhaustive.of
import uk.nhs.riskscore.*
import uk.nhs.riskscore.internal.BLEDistanceUnscentedKalmanFilter
import kotlin.math.absoluteValue

internal fun <A> Arb<A>.nonEmptyList(minSize: Int = 1) = Arb.list(this, minSize..100)

internal val positiveFiniteDoubles: Arb<Double> = Arb.positiveDoubles().filter { it != Double.POSITIVE_INFINITY }

internal val smallPositiveDoubles = Arb.numericDoubles(0.0, 100.0).map { it + 1e-3 }

internal val smallNumericDoubles = Arb.numericDoubles(-100.0, 100.0)

internal val powerLossParameters: Arb<PowerLossParameters> = Arb.bind(
    smallPositiveDoubles,
    smallNumericDoubles,
    smallNumericDoubles,
    ::PowerLossParameters)

internal val rssiParameters: Arb<RssiParameters> = Arb.bind(
    smallNumericDoubles,
    smallNumericDoubles,
    smallPositiveDoubles,
    ::RssiParameters
)

internal val expectedDistance = smallPositiveDoubles

internal val initialData: Arb<InitialData> = Arb.bind(
    smallPositiveDoubles,
    smallPositiveDoubles,
    ::InitialData
)

internal val observationType: Arb<ObservationType> = Arb.choose(
    1 to ObservationType.log,
    1 to ObservationType.gen)

internal val smootherParameters: Arb<SmootherParameters> = Arb.bind(
    Arb.constant(1.0),
    Arb.constant(0.0),
    Arb.constant(0.0)
) { alpha, beta, kappa ->
    SmootherParameters(
        alpha,
        beta,
        kappa
    )
}

internal val bleDistanceUnscentedKalmanFilter = Arb.bind(
    powerLossParameters,
    rssiParameters,
    expectedDistance,
    initialData,
    smootherParameters,
    observationType,
    ::BLEDistanceUnscentedKalmanFilter
)