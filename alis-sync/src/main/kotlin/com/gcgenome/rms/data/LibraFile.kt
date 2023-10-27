package com.gcgenome.rms.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class LibraFile (
    val createAt: LocalDateTime,
    val path: String,
    val type: String,
    val value: String?,
    val sampleId: UUID,
    val reportedAt: LocalDateTime?,
    val genomeBarcode: String,
    val serviceId: String
) {
    companion object {
        fun libraToModel(path: String, id: UUID, filePath: String): LibraFile {
            val part = path.split("/")
            val date = part[1] + part[2] + part[3]
            val genomeBarcode = date + part[4] + part[5]
            val serviceId = part[6]
            val type = part.last().split(".").last()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val localDate = LocalDate.parse(date, formatter).atStartOfDay()
            val value = "api/reports/$id"

            return LibraFile(
                createAt = localDate,
                path = filePath,
                type = type.uppercase(),
                value = value,
                sampleId = UUID.randomUUID(),
                reportedAt = localDate,
                genomeBarcode = genomeBarcode,
                serviceId = serviceId
            )
        }
    }
}