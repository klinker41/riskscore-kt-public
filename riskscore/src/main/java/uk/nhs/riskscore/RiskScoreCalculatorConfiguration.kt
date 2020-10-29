package uk.nhs.riskscore

/**
 * Configuration for the [RiskScoreCalculator]
 *
 * @param sampleResolution: The resolution of samples taken in seconds.
 * @param expectedDistance: The expected distance moved per second in meters.
 * @param minimumDistance: The minimum distance in meters. Any estimated distance lower than this will be replaced with this value.
 * @param rssiParameters: Parameters for the RSSI emission model.
 * @param powerLossParameters: Parameters used in the Friis function for RSSi to estimated distance calculation.
 * @param observationType: The observation model to use.
 * @param initialData: Prior values for the Kalman smoother.
 * @param smootherParameters: Parameters for the Kalman smoother
*/
public class RiskScoreCalculatorConfiguration(
    internal val sampleResolution: Double,
    internal val expectedDistance: Double,
    internal val minimumDistance: Double,
    internal val rssiParameters: RssiParameters,
    internal val powerLossParameters: PowerLossParameters,
    internal val observationType: ObservationType,
    internal val initialData: InitialData,
    internal val smootherParameters: SmootherParameters
) {
    public companion object {
        public val exampleConfiguration: RiskScoreCalculatorConfiguration get() =
            RiskScoreCalculatorConfiguration(
                sampleResolution = 1.0,
                expectedDistance = 0.1,
                minimumDistance = 1.0,
                rssiParameters = RssiParameters(
                    weightCoefficient = 0.1270547531082051,
                    intercept = 4.2309333657856945,
                    covariance = 0.4947614361027773
                ),
                powerLossParameters = PowerLossParameters(
                    wavelength = 0.125,
                    pathLossFactor = 20.0,
                    refDeviceLoss = 0.0
                ),
                observationType = ObservationType.log,
                initialData = InitialData(
                    mean = 2.0,
                    covariance = 10.0
                ),
                smootherParameters = SmootherParameters(
                    alpha = 1.0,
                    beta = 0.0,
                    kappa = 0.0
                )
        )
    }
}

/**
Parameters for the RSSI emissions model.
 */
public class RssiParameters(
    internal val weightCoefficient: Double,
    internal val intercept: Double,
    internal val covariance: Double
)

public enum class ObservationType {
    log, gen
}

/**
* @param wavelength: The signal wavelength in meters.
* @param pathLossFactor: The free-space loss factor.
* @param refDeviceLoss: The reference device losses.
 */
public class PowerLossParameters(
    internal val wavelength: Double,
    internal val pathLossFactor: Double,
    internal val refDeviceLoss: Double
)

/**
 * Initial data for the Kalman smoother
 */
public class InitialData(
    internal val mean: Double,
    internal val covariance: Double
)

public class SmootherParameters(
    internal val alpha: Double,
    internal val beta: Double,
    internal val kappa: Double
)
