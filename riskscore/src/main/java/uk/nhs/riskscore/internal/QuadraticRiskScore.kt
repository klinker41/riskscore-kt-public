package uk.nhs.riskscore.internal

internal class QuadraticRiskScore(private val minDistance: Double) {

    // Assumptions
    // All parameters are finite. This should be checked higher up the stack.
    // The elements of durations and distances are not large enough to cause sums
    // and products to become infinite.
    fun calculate(
        durations: List<Double>,
        distances: List<Double>,
        shouldNormalize: Boolean
    ): Double {
        val normDistances = distances.map { distance ->
            minDistance.squared() / distance.squared()
        }.map { it.clip(lower = 0.0, upper = 1.0) }

        val sumOfProduct = vectorProduct(durations, normDistances).sum()

        if (shouldNormalize) {
            return sumOfProduct / durations.sum()
        } else {
            return sumOfProduct
        }
    }
}