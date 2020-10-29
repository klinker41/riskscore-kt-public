package uk.nhs.kalman1d.internal

import io.kotest.core.spec.style.StringSpec
import uk.nhs.kalman1d.internal.kotest.shouldBeCloseTo
import kotlin.math.pow

internal class FilterTransformerCoordinatorTest: StringSpec({
    "initialTransform returns the observation if observation is provided and there is no noise" {
        val filterCoordinator = FilterTransformerCoordinator(
            Value(0.0),
            Value(0.0),
            Weights(1.0, 0.0, 0.0),
            { p, n -> p + n.pow(2) },
            { p, n -> p + n.pow(2) }
        )

        val observation: Value<Observation> = Value(2.0)
        val initialState = State(Value(0.0), Value(1.0))
        val initialEstimate = filterCoordinator.initialTransform(initialState, observation)

        initialEstimate.mean.rawValue shouldBeCloseTo observation.rawValue
        initialEstimate.covariance.rawValue shouldBeCloseTo 0.0
    }

    "transform returns the transformed state if observation is provided and there is noise" {
        val noiseTransform: (Double, Double) -> Double = { p, n -> p + n.pow(2) }

        val filterCoordinator = FilterTransformerCoordinator(
            Value(1.1),
            Value(1.1),
            Weights(1.0, 0.0, 0.0),
            noiseTransform,
            noiseTransform
        )

        val state = State(Value(0.0), Value(1.0))
        val estimate = filterCoordinator.transform(state, Value(1.0))

        estimate.mean.rawValue shouldBeCloseTo 0.3245614035087717
        estimate.covariance.rawValue shouldBeCloseTo 1.9919005847953215
    }
})