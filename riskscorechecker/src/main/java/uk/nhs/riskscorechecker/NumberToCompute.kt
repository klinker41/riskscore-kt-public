package uk.nhs.riskscorechecker

import kotlinx.cli.ArgType

sealed class NumberToCompute {
    object All: NumberToCompute() {
        override fun toString() = "Calculate All"
    }
    data class Number(val maximum: Int): NumberToCompute()
}

object NumberToComputeChoice: ArgType<NumberToCompute>(hasParameter = true) {
    override val description: kotlin.String
        get() = "{ Int }"

    override fun convert(value: kotlin.String, name: kotlin.String): NumberToCompute {
        val intValue = checkNotNull(value.toIntOrNull()) { "Option $name expected to be an integer" }
        check(intValue >= 0) { "Option $name expected to be a non-negative integer" }
        return NumberToCompute.Number(intValue)
    }
}