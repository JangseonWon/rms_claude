package com.idrsys.ailis.rms.application.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * 기관 응답 DTO
 */
data class OrganizationResponse(
    val id: Long,
    val serial: String,
    val name: String,

    @JsonProperty("registration_number")
    val registrationNumber: String? = null,

    @JsonProperty("nursing_number")
    val nursingNumber: String? = null,

    @JsonProperty("branch_code")
    val branchCode: String? = null,

    @JsonProperty("branch_name")
    val branchName: String? = null,

    @JsonProperty("employee_id")
    val employeeId: String? = null,

    @JsonProperty("employee_name")
    val employeeName: String? = null,

    @JsonProperty("employee_phone")
    val employeePhone: String? = null,

    val type: String? = null,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime? = null,

    @JsonProperty("created_by")
    val createdBy: String? = null,

    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime? = null,

    @JsonProperty("updated_by")
    val updatedBy: String? = null
)

/**
 * 기관 목록 응답 DTO
 */
data class OrganizationListResponse(
    val id: Long,
    val serial: String,
    val name: String,
    val type: String? = null,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime? = null
)
