package uk.nhs.riskscore.internal

import uk.nhs.riskscore.PowerLossParameters
import kotlin.math.PI
import kotlin.math.log10
import kotlin.math.pow

internal class FriisCalculator(val params: PowerLossParameters) {
    /**
     * The inverse Friis function. Computes distance from RSSI.
     *
     * @param rxCorrection: Correction shift in dBm for receiver device
     * @param txCorrection: Correction shift in dBm for transmitter device
     *
     * @returns: A function from RSSI to distance in meters
     */
    fun inverseFriis(rxCorrection: Double, txCorrection: Double): (Double) -> Double {
        val params = this.params
        return { rssi ->
            val adjRssi = rssi - rxCorrection - txCorrection
            val powerLoss = log10(params.wavelength / (4.0 * PI))
            val exponent = adjRssi - params.pathLossFactor * powerLoss - params.refDeviceLoss
            10.0.pow(-exponent / params.pathLossFactor)
        }
    }

    /**
     * The Friis function. Computes RSSI from distance.
     *  @param rxCorrection: Correction shift in dBm for receiver device
     *  @param txCorrection: Correction shift in dBm for transmitter device
     *  @returns: A function from distance in meters to RSSI.
     *
     *  Minimum distance is 1e-3, and minimum RSSI is -1e-3.
     */
    fun friis(rxCorrection: Double, txCorrection: Double): (Double) -> Double {
        val params = this.params
        return { distance ->
            val effectiveDistance = if (distance > 0.0) distance else 1e-3

            val result =
                -1 * log10(effectiveDistance) + rxCorrection + txCorrection + params.pathLossFactor * (log10(
                    params.wavelength / (4.0 * PI)
                )) + params.refDeviceLoss

            if (result < 0) result else -1e-3
        }
    }
}