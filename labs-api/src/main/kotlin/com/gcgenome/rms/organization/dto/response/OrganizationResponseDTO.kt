package com.gcgenome.rms.organization.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.util.*

data class OrganizationResponseDTO(
    @JsonIgnore
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
    val createAt: LocalDateTime? = null,
    @JsonIgnore
    val userId: LocalDateTime? = null,
)
