package com.gcgenome.rms.entity

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SampleEntity(
    val id: UUID,
    val barcode: String? = null,
    val serial: String,
    val count: Int,
    val samplingOn: LocalDate,
    val createAt: LocalDateTime,
    val sampleTypeId: UUID,
    val userId: UUID
)
