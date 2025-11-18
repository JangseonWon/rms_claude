package com.idrsys.ailis.rms.application.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * 추가 정보 응답 DTO
 */
data class ExtensionResponse(
    val id: Long,
    val code: String,
    val name: String,
    val description: String? = null,

    @JsonProperty("data_type")
    val dataType: String,

    @JsonProperty("is_required")
    val isRequired: Boolean,

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
 * 추가 정보 목록 응답 DTO
 */
data class ExtensionListResponse(
    val id: Long,
    val code: String,
    val name: String,

    @JsonProperty("data_type")
    val dataType: String,

    @JsonProperty("is_required")
    val isRequired: Boolean
)
