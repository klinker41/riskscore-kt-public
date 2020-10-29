package uk.nhs.kalman1d.internal.transformers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.checkAll
import uk.nhs.kalman1d.internal.*
import uk.nhs.kalman1d.internal.kotest.positiveValues
import uk.nhs.kalman1d.internal.kotest.shouldBeCloseTo
import uk.nhs.kalman1d.internal.kotest.states

internal class SigmaPointsGeneratorTest : StringSpec({
    "Each type of Sigma points has Weights.numberOfSigmaPoints" {
        checkAll(
            positiveValues<Scale>(),
            positiveValues<TransitionCovariance>(),
            positiveValues<ObservationCovariance>(),
            states
        ) { scale, transitionCovariance, observationCovariance, state ->
            val points = sigmaPoints(scale, transitionCovariance, observationCovariance, state)

            points.statePoints.array shouldHaveSize Weights.numberOfSigmaPoints
            points.observationSigmaPoints.array shouldHaveSize Weights.numberOfSigmaPoints
            points.transitionPoints.array shouldHaveSize Weights.numberOfSigmaPoints
        }
    }

    "The mean of the state Sigma points equals the state mean" {
        checkAll(
            positiveValues<Scale>(),
            positiveValues<TransitionCovariance>(),
            positiveValues<ObservationCovariance>(),
            states
        ) { scale, transitionCovariance, observationCovariance, state ->
            val points = sigmaPoints(scale, transitionCovariance, observationCovariance, state)
            meanOfPoints(points.statePoints) shouldBeCloseTo state.mean.rawValue
        }
    }

    "The mean of the transition Sigma points equals zero" {
        checkAll(
            positiveValues<Scale>(),
            positiveValues<TransitionCovariance>(),
            positiveValues<ObservationCovariance>(),
            states
        ) { scale, transitionCovariance, observationCovariance, state ->
            val points = sigmaPoints(scale, transitionCovariance, observationCovariance, state)
            meanOfPoints(points.transitionPoints) shouldBeCloseTo 0.0
        }
    }

    "The mean of the observation Sigma points equals to zero" {
        checkAll(
            positiveValues<Scale>(),
            positiveValues<TransitionCovariance>(),
            positiveValues<ObservationCovariance>(),
            states
        ) { scale, transitionCovariance, observationCovariance, state ->
            val points = sigmaPoints(scale, transitionCovariance, observationCovariance, state)
            meanOfPoints(points.observationSigmaPoints) shouldBeCloseTo 0.0
        }
    }
})

private fun <T : VectorTag> meanOfPoints(sigmaPoints: Vector<T>) =
    sigmaPoints.array.sum() / Weights.numberOfSigmaPoints

private fun sigmaPoints(
    scale: Value<Scale>,
    transitionCovariance: Value<TransitionCovariance>,
    observationCovariance: Value<ObservationCovariance>,
    state: State
): SigmaPoints =
    SigmaPointsGenerator(scale, transitionCovariance, observationCovariance).generatePointsFor(state)
