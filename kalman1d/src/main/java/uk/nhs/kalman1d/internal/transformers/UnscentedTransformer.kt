package uk.nhs.kalman1d.internal.transformers

import uk.nhs.kalman1d.internal.*
import uk.nhs.kalman1d.internal.PredictionState
import uk.nhs.kalman1d.internal.StateSigmaPoints
import uk.nhs.kalman1d.internal.Value
import uk.nhs.kalman1d.internal.ValueTag
import uk.nhs.kalman1d.internal.Vector
import uk.nhs.kalman1d.internal.Weights

internal typealias PredictionTransformer = UnscentedTransformer<StateMean, StateCovariance>

/**
The UnscentedTransformer computes the mean and covariance of Sigma points after they've been propagated though
a non-linear function.
 */
internal class UnscentedTransformer<T: ValueTag, U: ValueTag>(private val weights: Weights) {

    /**
    Compute the mean and covariance of the `stateSigmaPoints` using the Unscented
    Transform.

    ` mean = sum_{p = sigma point} meanWeight_p * p`
    `covariance = sum_{p = sigma point} covarianceWeight_p (p - mean) (p - mean)^T`
     */
    fun transform(stateSigmaPoints: Vector<StateSigmaPoints>): PredictionState<T, U> {
        val predictedMean = stateSigmaPoints.dotProduct(weights.meanWeights)
        val sigmaMeanDiff = stateSigmaPoints.map { it - predictedMean }
        val predictedCovariance = weights.covarianceWeights.dotProduct(sigmaMeanDiff.map { it * it })

        return PredictionState(
            Value(predictedMean),
            Value(predictedCovariance)
        )
    }
}
