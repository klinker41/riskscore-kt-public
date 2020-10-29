package uk.nhs.kalman1d.internal

import uk.nhs.kalman1d.internal.transformers.PredictionTransformer
import uk.nhs.kalman1d.internal.transformers.SigmaPointsGenerator
import uk.nhs.kalman1d.internal.transformers.SigmaPointsPropagator

internal class SmootherTransformerCoordinator(
    transitionCovariance: Value<TransitionCovariance>,
    observationCovariance: Value<ObservationCovariance>,
    val weights: Weights,
    transitionFunction: (Double, Double) -> Double) {

    private val sigmaPointsGenerator = SigmaPointsGenerator(
        weights.scale,
        transitionCovariance,
        observationCovariance)

    private val sigmaPointsPropagator = SigmaPointsPropagator<TransitionSigmaPoints>(transitionFunction)
    private val predictionTransformer = PredictionTransformer(weights)

    fun transform(state: State, previousState: State): State {
        val sigmaPoints = sigmaPointsGenerator.generatePointsFor(state)
        val propagatedStateSigmaPoints = sigmaPointsPropagator.propagate(
            sigmaPoints.statePoints,
            sigmaPoints.transitionPoints
        )
        val predictedState = predictionTransformer.transform(propagatedStateSigmaPoints)

        val crossCovariance = propagatedStateSigmaPoints
            .map { it - predictedState.mean.rawValue }
            .multiply(weights.covarianceWeights)
            .dotProduct(sigmaPoints.statePoints.map { it - state.mean.rawValue })

        val smootherGain = crossCovariance * predictedState.covariance.inverse().rawValue
        val meanCorrection = (previousState.mean.rawValue - predictedState.mean.rawValue) * smootherGain
        val covarianceCorrection = (previousState.covariance.rawValue - predictedState.covariance.rawValue) * smootherGain * smootherGain

        return State(
            Value(state.mean.rawValue + meanCorrection),
            Value(state.covariance.rawValue + covarianceCorrection)
        )
    }
}