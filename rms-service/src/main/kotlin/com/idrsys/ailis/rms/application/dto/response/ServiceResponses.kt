package com.idrsys.ailis.rms.application.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * 서비스 응답 DTO
 */
data class ServiceResponse(
    val id: Long,
    val serial: String,
    val name: String,
    val description: String? = null,
    val price: Long? = null,

    @JsonProperty("category_id")
    val categoryId: Long? = null,

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
 * 서비스 목록 응답 DTO
 */
data class ServiceListResponse(
    val id: Long,
    val serial: String,
    val name: String,
    val price: Long? = null,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime? = null
)
