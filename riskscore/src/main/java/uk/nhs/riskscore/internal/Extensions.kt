package uk.nhs.riskscore.internal

import kotlin.math.pow

internal fun Double.squared() = pow(2.0)

internal fun <A: Comparable<A>> A.clip(
    lower: A,
    upper: A
) = if (this < lower) lower else if (this > upper) upper else this

private fun vectorOperation(
    op: (Double, Double) -> Double
): (List<Double>, List<Double>) -> List<Double>
        = { l, r -> l.zip(r).map(uncurry(op)) }

internal val vectorProduct = vectorOperation(Double::times)

internal val vectorSum = vectorOperation(Double::plus)