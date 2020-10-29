package uk.nhs.support

import uk.nhs.riskscore.ScanInstance
import java.io.File
import java.lang.IllegalStateException

object ScanInstanceCSVDecoder {
    private const val secondsIndex = 0
    private const val valueIndex = 1
    private const val totalFields = 2

    fun decode(fromPath: String): List<ScanInstance> {
        return File(fromPath).useLines { lines ->
            lines.mapIndexed { idx, line ->
                val fields = line.split(",", limit = totalFields)

                val secondsField = fields.getOrNull(secondsIndex)?.toDoubleOrNull()
                val valueField = fields.getOrNull(valueIndex)?.toDoubleOrNull()

                if (secondsField == null || valueField == null) {
                    throw IllegalStateException("Line malformed $fromPath:$idx")
                }

                ScanInstance(valueField, secondsField)
            }.toList()
        }
    }
}