package com.gcgenome.rms.entity

import java.time.LocalDateTime
import java.util.*

data class RequestEntity(
    val id: UUID,
    val department: String? = null,
    val ward: String? = null,
    val physician: String? = null,
    val memo: String? = null,
    val genomePrice: Long? = null,
    val labsPrice: Long? = null,
    val createAt: LocalDateTime,
    val isEditable: Boolean = true,
    val isDeletable: Boolean = true,

    val serviceId: UUID,
    val sampleId: UUID,
    val organizationId: UUID,
    val patientId: UUID
)
