package com.gcgenome.rms.data

import java.time.LocalDateTime
import java.util.*

data class Report (
    val id : UUID,
    val createAt: LocalDateTime,
    val path: String,
    val type: String,
    val value: String?,
    val sampleId: UUID,
    val reportedAt: LocalDateTime?,
    val seqno: Int
)