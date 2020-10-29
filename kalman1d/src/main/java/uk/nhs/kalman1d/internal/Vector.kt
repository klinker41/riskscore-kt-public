package uk.nhs.kalman1d.internal

@Suppress("unused")
internal data class Vector<T : VectorTag>(val array: List<Double>)

internal sealed class VectorTag

internal sealed class WeightsTag: VectorTag()

internal object StateSigmaPoints : VectorTag()
internal object TransitionSigmaPoints : VectorTag()
internal object ObservationSigmaPoints : VectorTag()
internal object MeanWeights : WeightsTag()
internal object CovarianceWeights : WeightsTag()

internal fun <T : WeightsTag> weights(first: Double, common: Double) =
    Vector<T>(listOf(first) + List(Weights.numberOfSigmaPoints.dec()) { common })

internal fun <T: VectorTag> Vector<T>.map(transform: (Double) -> Double): Vector<T> =
    Vector(array.map(transform))

internal fun <T: VectorTag, U: VectorTag> Vector<T>.dotProduct(other: Vector<U>): Double =
    multiply(other).array.sum()

internal fun <T: VectorTag, U: VectorTag> Vector<T>.multiply(other: Vector<U>): Vector<T> =
    Vector(array.zip(other.array).map { (a, b) -> a * b })