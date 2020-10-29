package uk.nhs.kalman1d.internal

import uk.nhs.kalman1d.FilterListener
import uk.nhs.kalman1d.internal.transformers.MeasurementUpdater
import uk.nhs.kalman1d.internal.transformers.ObservationTransformer
import uk.nhs.kalman1d.internal.transformers.PredictionTransformer
import uk.nhs.kalman1d.internal.transformers.SigmaPointsGenerator
import uk.nhs.kalman1d.internal.transformers.SigmaPointsPropagator
import uk.nhs.kalman1d.nullFilterListener

internal class FilterTransformerCoordinator(
    transitionCovariance: Value<TransitionCovariance>,
    observationCovariance: Value<ObservationCovariance>,
    weights: Weights,
    transitionFunction: (Double, Double) -> Double,
    observationFunction: (Double, Double) -> Double,
    filterListener: FilterListener = nullFilterListener
) {
    val sigmaPointsGenerator = SigmaPointsGenerator(
        weights.scale,
        transitionCovariance,
        observationCovariance
    )
    val sigmaPointsPropagator = SigmaPointsPropagator<TransitionSigmaPoints>(transitionFunction)
    val predictionTransformer = PredictionTransformer(weights)
    val observationTransformer = ObservationTransformer(
        weights,
        SigmaPointsPropagator(observationFunction)
    )
    private val listen: (State) -> State = { state ->
        filterListener.states(0, state.mean.rawValue, state.covariance.rawValue)
        state
    }

    /**
     * Projects `state` forward to the next time-step.
     *
     * @param state The current state.
     * @param observation The observation associated with this time-step. The observation should be set to `nil` if there is no observation for this time-step.
     * @returns The updated state after the filter step has been performed for this time-step.
     */
    fun transform(state: State, observation: Value<Observation>?): State {
        val sigmaPoints = sigmaPointsGenerator.generatePointsFor(state)
        val propogatedSigmaPoints = sigmaPointsPropagator.propagate(
            sigmaPoints.statePoints,
            sigmaPoints.transitionPoints
        )
        return listen(predictionAndUpdate(
            propogatedSigmaPoints,
            sigmaPoints.observationSigmaPoints,
            observation
        ))
    }

    /**
     * Estimates the state at the first time-step.
     *
     * @param state The estimated initial state of the system.
     * @param observation The first observation. The observation should be set to `null` if there is no observation for this time-step.
     * @returns The estimated state, taking into account the first observation.
     */
    fun initialTransform(state: State, observation: Value<Observation>?): State {
        val sigmaPoints = sigmaPointsGenerator.generatePointsFor(state)
        return listen(predictionAndUpdate(
            sigmaPoints.statePoints,
            sigmaPoints.observationSigmaPoints,
            observation
        ))
    }

    private fun predictionAndUpdate(
        stateSigmaPoints: Vector<StateSigmaPoints>,
        observationSigmaPoints: Vector<ObservationSigmaPoints>,
        observation: Value<Observation>?
    ): State {
        val predictedState = predictionTransformer.transform(stateSigmaPoints)
        val predictedObservation = observationTransformer.transform(
            predictedState,
            stateSigmaPoints,
            observationSigmaPoints
        )
        return MeasurementUpdater.update(
            predictedState,
            predictedObservation,
            observation
        )
    }
}