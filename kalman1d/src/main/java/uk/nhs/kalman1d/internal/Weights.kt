package uk.nhs.kalman1d.internal

import kotlin.math.pow

internal class Weights(alpha: Double, beta: Double, kappa: Double) {
    val meanWeights: Vector<MeanWeights>
    val covarianceWeights: Vector<CovarianceWeights>
    val scale: Value<Scale>

    companion object {
        val augmentedDimension = 3.0
        val numberOfSigmaPoints = 2 * augmentedDimension.toInt() + 1
    }

    init {
        val lambda = alpha.pow(2) * (augmentedDimension + kappa) - augmentedDimension
        scale = Value(augmentedDimension + lambda)

        val common = scale.inverse().rawValue * 0.5
        meanWeights = weights(lambda / scale.rawValue, common)
        covarianceWeights = weights((lambda / scale.rawValue) + (1 - alpha.pow(2) + beta), common)
    }
}