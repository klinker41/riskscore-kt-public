package uk.nhs.riskscore

import uk.nhs.riskscore.internal.assertionFailure
import uk.nhs.riskscore.internal.pad
import kotlin.math.ln

/**
 * An adapter type for the EN API.
 *
 *  NB: The internal constructor is used by tests as this matches the type of data provided
 *  from the Python simulations.
 *
 */
public data class ScanInstance(
    internal val value: Double,
    internal val secondsSinceLastScan: Double
) {
    /**
     * @param attenuationValue The attenuation value of this scan.
     * @param secondsSinceLastScan The elapsed time since the previous scan.
     *
     */
    public constructor(
        attenuationValue: Int,
        secondsSinceLastScan: Int
    ) : this(attenuationValue.toDouble(),secondsSinceLastScan.toDouble())
}

internal data class Observation(
    var sequenceNumber: Int,
    var value: Double?
) {
    companion object {

        fun makeObservations(
            instances: List<ScanInstance>,
            sampleResolution: Double
        ): List<Observation> {
            val firstInstance = instances.firstOrNull() ?: run {
                assertionFailure { "makeObservations: called with 0 instances" }
                return emptyList()
            }

            // Algorithm assumes that the first observation is unmasked so we do not pad before
            // the first ScanInstance.
            val resetFirst = ScanInstance(firstInstance.value, 0.0)
            val resetInstances = listOf(resetFirst) + instances.drop(1)

            return resetInstances.fold(Pair(0.0, mutableListOf<Observation>())) { current, next ->
                val (lastSeq, observations) = current
                val nextSeq = lastSeq + (next.secondsSinceLastScan / sampleResolution)

                observations.add(Observation(nextSeq.toInt(), ln(next.value)))
                Pair(nextSeq, observations)
            }.second
                .pad({ Observation(it, null) }, { it.sequenceNumber })
                .map { observation ->
                    // replace a non-finite Double value with `null`.
                    val value = observation.value
                    if (value != null && !value.isFinite()) {
                        Observation(observation.sequenceNumber, null)
                    } else {
                        observation
                    }
                }
        }
    }
}