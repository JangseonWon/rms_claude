package com.gcgenome.rms.entity

import java.time.LocalDateTime
import java.util.*

data class UserSampleTypeEntity(
    val id: UUID,
    val serial: String,
    val createAt: LocalDateTime,
    val userId: UUID,
    val sampleTypeId: UUID
)
