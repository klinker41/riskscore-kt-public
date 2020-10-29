package uk.nhs.riskscore.internal

import uk.nhs.kalman1d.UnscentedKalmanSmoother
import uk.nhs.riskscore.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * A wrapper type around [UnscentedKalmanSmoother] adpated for smoothing scalar (RSSI) input.
 */
internal class BLEDistanceUnscentedKalmanFilter(
    powerLossParameters: PowerLossParameters,
    rssiParameters: RssiParameters,
    expectedDistance: Double,
    initialData: InitialData,
    smootherParameters: SmootherParameters,
    observationType: ObservationType
) {
    private val rssiAdapter: RssiAdapter = RssiAdapter(
        powerLossParameters,
        rssiParameters.weightCoefficient,
        rssiParameters.intercept,
        observationType
    )

    private val filter: UnscentedKalmanSmoother = UnscentedKalmanSmoother(
        initialData.mean,
        initialData.covariance,
        expectedDistance.pow(2),
        rssiParameters.covariance,
        smootherParameters.alpha,
        smootherParameters.beta,
        smootherParameters.kappa,
        ::transitionFunction,
        ::observationFunction
    )

    fun smooth(observations: List<Double?>) = filter.smooth(observations)

    fun transitionFunction(state: Double, noise: Double) = abs(state + noise)

    fun observationFunction(state: Double, noise: Double): Double {
        return rssiAdapter.observationTransform(abs(state)) + noise
    }
}
