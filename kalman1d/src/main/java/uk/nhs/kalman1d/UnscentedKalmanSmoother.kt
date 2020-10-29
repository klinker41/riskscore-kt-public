package uk.nhs.kalman1d

import uk.nhs.kalman1d.internal.*
import uk.nhs.kalman1d.internal.Observation
import uk.nhs.kalman1d.internal.ObservationCovariance
import uk.nhs.kalman1d.internal.State
import uk.nhs.kalman1d.internal.TransitionCovariance
import uk.nhs.kalman1d.internal.Value
import uk.nhs.kalman1d.internal.Weights

/**
 * An implementation of the Augmented Unscented Kalman Smoother for one dimensional state
 *
 * @param initialStateMean: The mean of the initial state distribution
 * @param initialStateCovariance: The covariance of the initial state distribution
 * @param transitionCovariance: The transition noise covariance
 * @param observationCovariance: The observation noise covariance
 * @param alpha
 * @param beta,
 * @param kappa: Parameters for the Sigma points calculation
 * @param transitionFunction: A function of state and transition covariance that projects the state forwards by 1 time-step.
 * @param observationFunction: A function of state and observation covariance that projects the state into the measurement space.
 * @param filterListener: A diagnostics listener for reporting intermediate calculations
 *
 */
public class UnscentedKalmanSmoother(
    initialStateMean: Double,
    initialStateCovariance: Double,
    transitionCovariance: Double,
    observationCovariance: Double,
    alpha: Double,
    beta: Double,
    kappa: Double,
    private val transitionFunction: (Double, Double) -> Double,
    private val observationFunction: (Double, Double) -> Double,
    private val filterListener: FilterListener = nullFilterListener
) {
    private val initialState = State(Value(initialStateMean), Value(initialStateCovariance))
    private val transitionCovariance = Value<TransitionCovariance>(transitionCovariance)
    private val observationCovariance = Value<ObservationCovariance>(observationCovariance)
    private val weights = Weights(alpha, beta, kappa)

    public fun smooth(observationData: List<Double?>): List<Pair<Double, Double>> {
        check(observationData.isNotEmpty()) { "There must be at least one observation" }
        val headObservation: Value<Observation>? = observationData.first()?.let(::Value)
        val tailObservations: List<Value<Observation>?> = observationData.drop(1).map { datum: Double? ->
            val observation: Value<Observation>? = datum?.let(::Value)
            observation
        }

        val filterCoordinator = FilterTransformerCoordinator(
            transitionCovariance,
            observationCovariance,
            weights,
            transitionFunction,
            observationFunction,
            filterListener
        )

        val firstState = filterCoordinator.initialTransform(initialState, headObservation)

        val filteredResult = tailObservations.fold(Pair(firstState, mutableListOf(firstState))) { (previousState, states), nextObservation ->
            val nextState = filterCoordinator.transform(previousState, nextObservation)
            states.add(nextState)
            Pair(nextState, states)
        }.second

        val smootherCoordinator = SmootherTransformerCoordinator(
            transitionCovariance,
            observationCovariance,
            weights,
            transitionFunction
        )

        val lastResult = filteredResult.last()

        return filteredResult.dropLast(1).asReversed().fold(Pair(lastResult, mutableListOf(lastResult))) { (previousState, states), currentState ->
            val nextState = smootherCoordinator.transform(currentState, previousState)
            states.add(nextState)
            Pair(nextState, states)
        }.second.asReversed().map { state -> Pair(state.mean.rawValue, state.covariance.rawValue) }
    }
}

/**
 * Diagnostics listener for reporting intermediate results
 */
public interface FilterListener {
    public fun states(index: Int, mean: Double, covariance: Double)
}

public val nullFilterListener: FilterListener = object : FilterListener {
    override fun states(index: Int, mean: Double, covariance: Double) {}
}