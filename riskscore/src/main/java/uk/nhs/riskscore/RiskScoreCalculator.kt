package uk.nhs.riskscore

import uk.nhs.riskscore.internal.BLEDistanceUnscentedKalmanFilter
import uk.nhs.riskscore.internal.GammaDistribution
import uk.nhs.riskscore.internal.QuadraticRiskScore
import uk.nhs.riskscore.internal.squared

public class RiskScoreCalculator (configuration: RiskScoreCalculatorConfiguration) {
    private val sampleResolution: Double = configuration.sampleResolution
    private val smoother: BLEDistanceUnscentedKalmanFilter = BLEDistanceUnscentedKalmanFilter(
        configuration.powerLossParameters,
        configuration.rssiParameters,
        configuration.expectedDistance,
        configuration.initialData,
        configuration.smootherParameters,
        configuration.observationType
    )
    private val riskScore: QuadraticRiskScore = QuadraticRiskScore(configuration.minimumDistance)

    public fun calculate(instances: List<ScanInstance>): Double {
        val observations = Observation.makeObservations(instances, sampleResolution)
        val (distances, durations) = smoother.smooth(observations.map(Observation::value)).map { (mean, covariance) ->
            val scale = 1.0 / (mean / covariance)
            val shape = mean.squared() / covariance
            val distance = GammaDistribution.median(shape, scale)
            // TODO: confirm the units of sampleResolution and store that in the configuration.
            Pair(distance, sampleResolution / 60.0)
        }.unzip()
        return riskScore.calculate(durations, distances, shouldNormalize = false)
    }
}