package uk.nhs.kalman1d.internal.transformers

import uk.nhs.kalman1d.internal.*
import kotlin.math.sqrt

/**
 * SigmaPointsGenerator chooses a collection of points in the State space that represent the Guassian
 * distribution described by the current State.
 *
 * The Sigma points are chosen using Van der Merwe’s scaled Sigma point algorithm. This distributes the
 * Sigma points around the State's mean and at a distance proportional to the State's covariance.
 *
 * The idea is that after these points are projected forwards in time a new Gaussian can be
 * reconstructed from them as part of the Unscented transform.
 *
 * In our case the state, transition and observation dimensions are all equal to one so the
 * algorithm produces 7 points for of each type. NB: The duplication of points is necessary
 * due to how the mean and covariance weights are used to compute mean and covariance of the Sigma points.
 *
 */
internal class SigmaPointsGenerator(
    private val scale: Value<Scale>,
    private val transitionCovariance: Value<TransitionCovariance>,
    private val observationCovariance: Value<ObservationCovariance>
) {
    fun generatePointsFor(state: State): SigmaPoints {
        val rawScale = scale.rawValue
        val mean = state.mean.rawValue
        val stateFactor = sqrt(rawScale * state.covariance.rawValue)
        val transitionFactor = sqrt(rawScale * transitionCovariance.rawValue)
        val observationFactor = sqrt(rawScale * observationCovariance.rawValue)

        return SigmaPoints(
            Vector(listOf(mean, mean + stateFactor, mean,              mean,              mean - stateFactor, mean,              mean)),
            Vector(listOf(0.0,  0.0,                transitionFactor,  0.0,               0.0,                -transitionFactor, 0.0)),
            Vector(listOf(0.0,  0.0,                0.0,               observationFactor, 0.0,                0.0,               -observationFactor))
        )
    }
}

internal data class SigmaPoints(
    val statePoints: Vector<StateSigmaPoints>,
    val transitionPoints: Vector<TransitionSigmaPoints>,
    val observationSigmaPoints: Vector<ObservationSigmaPoints>
)