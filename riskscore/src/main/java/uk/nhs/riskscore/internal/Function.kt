package uk.nhs.riskscore.internal

internal fun <A> identity(a: A) = a

internal fun <A, B, C> uncurry(f: (A, B) -> C): (Pair<A, B>) -> C = { (a, b) -> f(a, b) }