package uk.nhs.kalman1d.internal.transformers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import uk.nhs.kalman1d.internal.StateSigmaPoints
import uk.nhs.kalman1d.internal.TransitionSigmaPoints
import uk.nhs.kalman1d.internal.Vector

internal class SigmaPointsPropagatorTest : StringSpec() {
    private val sum: Transformation = { p, n -> p + n }
    private val sigmaPointsPropagator = SigmaPointsPropagator<TransitionSigmaPoints>(sum)

    init {
        "Applies function to each pair of components" {
            val statePoints = Vector<StateSigmaPoints>(listOf(0.1, 0.2, 0.3))
            val noise = Vector<TransitionSigmaPoints>(listOf(0.4, 0.6, 0.8))
            val propagatedPoints = sigmaPointsPropagator.propagate(statePoints, noise)

            propagatedPoints.array shouldBe listOf(0.5, 0.8, 1.1)
        }
    }
}
