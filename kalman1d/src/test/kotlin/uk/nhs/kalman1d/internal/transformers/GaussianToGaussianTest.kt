package uk.nhs.kalman1d.internal.transformers

import io.kotest.core.spec.style.StringSpec
import uk.nhs.kalman1d.internal.*
import uk.nhs.kalman1d.internal.kotest.shouldMatch

@Suppress("MoveLambdaOutsideParentheses")
internal class GaussianToGaussianTest : StringSpec() {
   private val noValue = 0.0
   private val arbitraryValue = 0.1
   private val noiseValue = 1.0

   private val arbitraryWeights = Weights(noiseValue, noValue, noValue)
   private  val initialState = State(Value(noValue), Value(noiseValue))

   init {
      "State Unscented transform with no-noise is identity" {
         unscentedState(Value(noValue), Value(arbitraryValue), { it.transitionPoints })
            .shouldMatch(initialState.mean.rawValue, initialState.covariance.rawValue)
      }

      "Observation Unscented transform with no-noise is identity" {
         unscentedState(Value(arbitraryValue), Value(noValue), { it.observationSigmaPoints })
            .shouldMatch(initialState.mean.rawValue, initialState.covariance.rawValue)
      }

      "State Unscented transform with additive noise is identity for mean, additive for covariance" {
         val transitionNoise = Value<TransitionCovariance>(noiseValue)

         unscentedState(transitionNoise, Value(arbitraryValue), { it.transitionPoints })
            .shouldMatch(initialState.mean.rawValue, initialState.covariance.rawValue + transitionNoise.rawValue)
      }

      "Observation Unscented transform with additive noise is identity for mean, additive for covariance" {
         val observationNoise = Value<ObservationCovariance>(noiseValue)

         unscentedState( Value(arbitraryValue), observationNoise, { it.observationSigmaPoints })
            .shouldMatch(initialState.mean.rawValue, initialState.covariance.rawValue + observationNoise.rawValue)
      }
   }

   private fun <SigmaPointsTag: VectorTag> unscentedState(
      transitionNoise: Value<TransitionCovariance>,
      observationNoise: Value<ObservationCovariance>,
      points: (SigmaPoints) -> Vector<SigmaPointsTag>
   ): PredictionState<StateMean, StateCovariance> {
      val sigmaPoints = SigmaPointsGenerator(
         arbitraryWeights.scale,
         transitionNoise,
         observationNoise
      ).generatePointsFor(initialState)

      val propagatedState =
         SigmaPointsPropagator<SigmaPointsTag> { p, n -> p + n }.propagate(
            sigmaPoints.statePoints,
            points(sigmaPoints)
         )

      return UnscentedTransformer<StateMean, StateCovariance>(arbitraryWeights).transform(propagatedState)
   }
}