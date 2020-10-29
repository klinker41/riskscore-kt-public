package uk.nhs.kalman1d.internal.transformers

import uk.nhs.kalman1d.internal.StateSigmaPoints
import uk.nhs.kalman1d.internal.Vector
import uk.nhs.kalman1d.internal.VectorTag

internal typealias Transformation = (Double, Double) -> Double

/**
SigmaPointsPropagator propagates each state Sigma point though the transformation function.
 */
internal class SigmaPointsPropagator<S: VectorTag>(val transformation: Transformation) {
    /**
     * This expects points and noise to be of the same, non-zero length. This is not checked.
     * @param points A vector of state Sigma points.
     * @param noise A vector of Sigma points representing the noise of the process (transition or observation noise).
     * @returns The propagated state Sigma points.
     */
    fun propagate(points: Vector<StateSigmaPoints>, noise: Vector<S>): Vector<StateSigmaPoints> =
        Vector(points.array.zip(noise.array).map { (p, n) -> transformation(p, n) })
}