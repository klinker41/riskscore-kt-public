package uk.nhs.kalman1d.internal

internal data class PredictionState<T : ValueTag, U : ValueTag>(
    val mean: Value<T>,
    val covariance: Value<U>
)

internal typealias State = PredictionState<StateMean, StateCovariance>
internal typealias ObservationState = PredictionState<Observation, ObservationCovariance>