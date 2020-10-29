package uk.nhs.kalman1d.internal.transformers

import uk.nhs.kalman1d.internal.Observation
import uk.nhs.kalman1d.internal.State
import uk.nhs.kalman1d.internal.Value

internal object MeasurementUpdater {
    /**
     * Update the state based on the prediction of the observation.
     *
     * @param predictedState The current state after the prediction step.
     * @param predictedObservation The state after the sigma points have been mapped to the measurement space.
     * @param maskedObservation The current observation.
     * @returns The new `predictedState` updated using the `predictedObservation`.
     */
    fun update(
        predictedState: State,
        predictedObservation: ObservationPrediction,
        maskedObservation: Value<Observation>?
    ): State {
        val observation = maskedObservation ?: return predictedState

        val kalmanGain =
            predictedObservation.crossCovariance.rawValue * predictedObservation.state.covariance.inverse().rawValue
        val residual = observation.rawValue - predictedObservation.state.mean.rawValue

        return State(
            Value(predictedState.mean.rawValue + residual * kalmanGain),
            Value(predictedState.covariance.rawValue - predictedObservation.crossCovariance.rawValue * kalmanGain)
        )
    }
}