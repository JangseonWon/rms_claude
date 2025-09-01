package com.gcgenome.rms.entity

import java.time.LocalDateTime
import java.util.*

data class OrganizationEntity(
    val id: UUID,
    val serial: String,
    val name: String,
    val registrationNumber: String? = null,
    val nursingNumber: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val employeePhone: String? = null,
    val type: String? = null,
    val createAt: LocalDateTime,
    val userId: UUID
)
