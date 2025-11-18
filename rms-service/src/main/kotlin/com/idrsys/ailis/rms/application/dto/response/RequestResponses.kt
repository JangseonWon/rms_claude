package com.idrsys.ailis.rms.application.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 의뢰 응답 DTO
 */
data class RequestResponse(
    val id: Long,
    val serial: String,

    @JsonProperty("request_date_from")
    val requestDateFrom: LocalDate,

    @JsonProperty("request_date_to")
    val requestDateTo: LocalDate,

    val department: String? = null,
    val ward: String? = null,
    val physician: String? = null,
    val memo: String? = null,

    @JsonProperty("genome_price")
    val genomePrice: Long? = null,

    @JsonProperty("labs_price")
    val labsPrice: Long? = null,

    val organization: OrganizationResponse,
    val patient: PatientResponse,
    val sample: SampleResponse,
    val extensions: List<RequestExtensionResponse>? = null,

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
 * 의뢰 목록 응답 DTO
 */
data class RequestListResponse(
    val id: Long,
    val serial: String,

    @JsonProperty("request_date_from")
    val requestDateFrom: LocalDate,

    @JsonProperty("request_date_to")
    val requestDateTo: LocalDate,

    @JsonProperty("organization_name")
    val organizationName: String,

    @JsonProperty("patient_name")
    val patientName: String,

    @JsonProperty("created_at")
    val createdAt: LocalDateTime? = null
)

/**
 * 의뢰 추가 정보 응답 DTO
 */
data class RequestExtensionResponse(
    val code: String,
    val name: String,
    val value: String
)
