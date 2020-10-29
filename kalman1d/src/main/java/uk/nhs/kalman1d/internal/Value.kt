package uk.nhs.kalman1d.internal

internal data class Value<T: ValueTag>(val rawValue: Double) {
    fun <U: ValueTag> translate(other: Value<U>): Value<T>
            = Value(other.rawValue + other.rawValue)

    fun inverse(): Value<T> {
        require(rawValue != 0.0) { "Division by zero" }
        return Value<T>(1.0 / rawValue)
    }

    operator fun unaryMinus() = Value<T>(-rawValue)

    operator fun plus(other: Value<T>) = Value<T>(rawValue + other.rawValue)
}

internal sealed class ValueTag
internal object Scale: ValueTag()
internal object StateMean: ValueTag()
internal object StateCovariance: ValueTag()
internal object Observation: ValueTag()
internal object TransitionCovariance: ValueTag()
internal object ObservationCovariance: ValueTag()

internal infix fun <T: ValueTag> Double.divideValue(value: Value<T>): Double = this / value.rawValue