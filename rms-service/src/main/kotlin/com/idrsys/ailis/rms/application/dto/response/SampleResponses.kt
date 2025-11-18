package com.idrsys.ailis.rms.application.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 샘플 응답 DTO
 */
data class SampleResponse(
    val id: Long,
    val count: Int,
    val age: Int? = null,

    @JsonProperty("sampling_on")
    val samplingOn: LocalDate,

    val type: SampleTypeResponse,

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
 * 샘플 유형 응답 DTO
 */
data class SampleTypeResponse(
    val id: Long,
    val serial: String,
    val name: String,
    val description: String? = null,

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
 * 샘플 유형 목록 응답 DTO
 */
data class SampleTypeListResponse(
    val id: Long,
    val serial: String,
    val name: String,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime? = null
)
