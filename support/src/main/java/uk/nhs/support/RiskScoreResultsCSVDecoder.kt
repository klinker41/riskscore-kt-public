package uk.nhs.support

import java.io.File
import java.lang.IllegalStateException

object RiskScoreResultsCSVDecoder {
    private const val scenarioCsvFieldIndex = 7
    private const val score16FieldIndex = 4
    private const val totalFields = 8

    fun decode(fromPath: String): Map<InstanceName, RiskScoreValue> {
        return File(fromPath).useLines { lines ->
            lines.drop(1).foldIndexed(mutableMapOf()) { idx, result, line ->
                val fields = line.split(",", limit = totalFields)

                val score16 = fields.getOrNull(score16FieldIndex)?.toDoubleOrNull() ?: throw IllegalStateException("Malformed score line $fromPath:${idx + 1}, $fields")
                val name = fields.getOrNull(scenarioCsvFieldIndex) ?: throw IllegalStateException("Malformed name line $fromPath:${idx + 1}, $fields")

                result[InstanceName(name.trim('"'))] = RiskScoreValue(score16)
                result
            }
        }
    }
}