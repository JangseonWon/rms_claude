package com.idrsys.ailis.rms.application.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 환자 응답 DTO
 */
data class PatientResponse(
    val id: Long,
    val serial: String,
    val name: String,
    val sex: String? = null,
    val age: Int? = null,
    val birth: LocalDate? = null,

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
 * 환자 목록 응답 DTO
 */
data class PatientListResponse(
    val id: Long,
    val serial: String,
    val name: String,
    val sex: String? = null,
    val age: Int? = null,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime? = null
)
