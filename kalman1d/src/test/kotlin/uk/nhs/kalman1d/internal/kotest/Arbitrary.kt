package uk.nhs.kalman1d.internal.kotest

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.numericDoubles
import uk.nhs.kalman1d.internal.StateCovariance
import uk.nhs.kalman1d.internal.StateMean
import uk.nhs.kalman1d.internal.State
import uk.nhs.kalman1d.internal.Value
import uk.nhs.kalman1d.internal.ValueTag

internal val smallPositiveDoubles = Arb.numericDoubles(0.0, 100.0).map { it + 1e-3 }
internal val smallDoubles = Arb.numericDoubles(-100.0, 100.0)

internal fun <T: ValueTag> positiveValues(): Arb<Value<T>> = smallPositiveDoubles.map(::Value)

internal fun <T: ValueTag> values(): Arb<Value<T>> = smallDoubles.map(::Value)

internal val states = Arb.bind(
    positiveValues(),
    positiveValues(),
    ::State)