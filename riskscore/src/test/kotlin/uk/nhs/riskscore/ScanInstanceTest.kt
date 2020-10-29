package uk.nhs.riskscore

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import kotlin.math.ln

internal class ScanInstanceTest: StringSpec() {

    init {
        "make observations from ScanInstances each with single values" {
            assertObservations(exampleDataSingleValues, 1.0)
        }

        "make observations from ScanInstances with Public API" {
            assertObservations(exampleDataPublicApi, 1.0)
        }

        "make observations from ScanInstances with Public API half resolution" {
            assertObservations(exampleDataPublicApi, 0.5)
        }

        "make observations from ScanInstances with infinite value" {
            assertObservations(exampleDataSingleNonFinite, 1.0)
        }
    }

    fun assertObservations(inputData: List<ScanInstance>, sampleResolution: Double) {
        val observations = Observation.makeObservations(inputData, sampleResolution)
        val intervalSum = inputData.drop(1).map(ScanInstance::secondsSinceLastScan).sum()

        observations.size shouldBeExactly ((1.0 / sampleResolution) * intervalSum).toInt() + 1

        observations.map(Observation::value)
            .filterNotNull() shouldBe inputData.map(ScanInstance::value).filter(Double::isFinite)
            .map(::ln)
    }
}