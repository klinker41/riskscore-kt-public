package uk.nhs.kalman1d.internal.transformers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import uk.nhs.kalman1d.internal.ObservationState
import uk.nhs.kalman1d.internal.State
import uk.nhs.kalman1d.internal.Value
import uk.nhs.kalman1d.internal.kotest.shouldBeCloseTo

internal class MeasurementUpdaterTest : StringSpec({

    "Returns predicted state if observation is missing" {
        val arbitraryPredictedState = State(Value(0.0), Value(1.0))
        val arbitraryPredictedObservation = ObservationPrediction(
            ObservationState(Value(2.0), Value(3.0)),
            Value(1.0)
        )

        MeasurementUpdater.update(
            arbitraryPredictedState,
            arbitraryPredictedObservation,
            null
        ) shouldBe arbitraryPredictedState
    }

    "Returns approx predicted state if cross-covariance is small" {
        val arbitraryPredictedState = State(Value(0.0), Value(1.0))
        val arbitraryPredictedObservation = ObservationPrediction(
            ObservationState(Value(3.0), Value(4.0)),
            Value(1e-10)
        )

        val updated = MeasurementUpdater.update(
            arbitraryPredictedState,
            arbitraryPredictedObservation,
            Value(0.0)
        )

        updated.mean.rawValue shouldBeCloseTo arbitraryPredictedState.mean.rawValue
        updated.covariance.rawValue shouldBeCloseTo arbitraryPredictedState.covariance.rawValue
    }

    "Returns correction by residual if cross-covariance is equal to predicted covariance" {
        val observation = 0.0
        val observationMean = 2.0
        val crossCovariance = 2.0
        val arbitraryPredictedState = State(Value(0.0), Value(1.0))
        val arbitraryPredictedObservation = ObservationPrediction(
            ObservationState(Value(observationMean), Value(2.0)),
            Value(crossCovariance)
        )

        val updated = MeasurementUpdater.update(
            arbitraryPredictedState,
            arbitraryPredictedObservation,
            Value(observation)
        )

        updated.mean.rawValue shouldBeCloseTo arbitraryPredictedState.mean.rawValue + (observation - observationMean)
        updated.covariance.rawValue shouldBeCloseTo arbitraryPredictedState.covariance.rawValue - crossCovariance
    }
})
