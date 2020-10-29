package uk.nhs.riskscore.internal

import org.apache.commons.math3.distribution.GammaDistribution as Gamma

/**
* The Gamma distribution
*
* @param shape: The shape parameter of the distribution
* @param scale: The scale factor to apply
 */
internal class GammaDistribution(
    shape: Double,
    private val scale: Double
) {
    private val distribution = Gamma(shape, 1.0)

    /**
     * Computes the inverse cumulative distrubution function of this Gamma distribution
     *
     * i.e the function computes a value x of an observation from the Gamma distribution is in
     * the range [0,x] with given probability p
     *
     * @param probability: A probability value between `0` and `1`
     * @returns: A value x such that observations from the Gamma distribution is
     * in the range [0,x] occur with the given `probability`.
     */
    fun inverseCDF(probability: Double) = distribution.inverseCumulativeProbability(probability) * scale

    companion object {
        /**
         * Convenience function to compute the median value of an observation for a Gamma distribution with a specified shape and scale.
         */
        fun median(shape: Double, scale: Double): Double =
            GammaDistribution(shape, scale).inverseCDF(0.5)
    }
}