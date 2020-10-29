package uk.nhs.kalman1d.internal.transformers

import uk.nhs.kalman1d.internal.*
import uk.nhs.kalman1d.internal.Observation
import uk.nhs.kalman1d.internal.ObservationCovariance
import uk.nhs.kalman1d.internal.ObservationSigmaPoints
import uk.nhs.kalman1d.internal.Weights

/**
 * ObservationTransformer computes the mean and covariance of sigma points after they've been propagated
 * through a measurement function. The cross-covariance between state and output is also computed.
 */
internal class ObservationTransformer(
    private val weights: Weights,
    private val sigmaPointsPropagator: SigmaPointsPropagator<ObservationSigmaPoints>
) {
    private val unscentedTransformer = UnscentedTransformer<Observation, ObservationCovariance>(weights)

    /**
     * The mean, covariance and cross-covariance are computed as follows:
     *
     * observationMean = sum_{p = h(sigma point)} meanWeight_p * p
     * observationCovariance = sum_{q = h(sigma point)} covarianceWeight_q (q - observationMean)^2
     * cross-covariance = sum_{p = sigma point} covarianceWeight_p (p - stateMean) (h(p) - observationMean)
     *
     * where h stands for the observation transformation.
     */
    fun transform(
        state: State,
        stateSigmaPoints: Vector<StateSigmaPoints>,
        observationSigmaPoints: Vector<ObservationSigmaPoints>
    ): ObservationPrediction {

        val observationPoints = sigmaPointsPropagator.propagate(stateSigmaPoints, observationSigmaPoints)
        val observationPrediction = unscentedTransformer.transform(observationPoints)

        val crossCovariance: Double =
            stateSigmaPoints
                .map { it - state.mean.rawValue }
                .multiply(weights.covarianceWeights)
                .dotProduct(observationPoints.map { it - observationPrediction.mean.rawValue })

        return ObservationPrediction(
            observationPrediction,
            Value(crossCovariance)
        )
    }
}

internal data class ObservationPrediction(
    val state: ObservationState,
    val crossCovariance: Value<ObservationCovariance>)
