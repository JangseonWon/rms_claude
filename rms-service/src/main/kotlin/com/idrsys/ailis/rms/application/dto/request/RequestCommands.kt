package com.idrsys.ailis.rms.application.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import org.openapitools.jackson.nullable.JsonNullable
import java.time.LocalDate

/**
 * 의뢰 생성 Command
 */
data class CreateRequestCommand(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String,

    @field:NotNull(message = "{validation.notNull}")
    @field:PastOrPresent(message = "{validation.pastOrPresent}")
    @JsonProperty("request_date_from")
    val requestDateFrom: LocalDate,

    @field:NotNull(message = "{validation.notNull}")
    @field:PastOrPresent(message = "{validation.pastOrPresent}")
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

    @field:Valid
    @field:NotNull(message = "{validation.notNull}")
    val organization: OrganizationCommand,

    @field:Valid
    @field:NotNull(message = "{validation.notNull}")
    val patient: PatientCommand,

    @field:Valid
    @field:NotNull(message = "{validation.notNull}")
    val sample: SampleCommand,

    val extensions: List<@Valid RequestExtensionCommand>? = null
)

/**
 * 의뢰 수정 Command (전체 업데이트)
 */
data class UpdateRequestCommand(
    val department: String? = null,
    val ward: String? = null,
    val physician: String? = null,
    val memo: String? = null,

    @JsonProperty("genome_price")
    val genomePrice: Long? = null,

    @JsonProperty("labs_price")
    val labsPrice: Long? = null,

    @field:Valid
    val organization: OrganizationCommand? = null,

    @field:Valid
    val patient: PatientCommand? = null,

    @field:Valid
    val sample: SampleCommand? = null,

    val extensions: List<@Valid RequestExtensionCommand>? = null
)

/**
 * 의뢰 부분 수정 Command (PATCH)
 */
data class PatchRequestCommand(
    @JsonProperty("department")
    val department: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("ward")
    val ward: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("physician")
    val physician: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("memo")
    val memo: JsonNullable<String> = JsonNullable.undefined(),

    @JsonProperty("genome_price")
    val genomePrice: JsonNullable<Long> = JsonNullable.undefined(),

    @JsonProperty("labs_price")
    val labsPrice: JsonNullable<Long> = JsonNullable.undefined(),

    @JsonProperty("organization")
    val organization: JsonNullable<@Valid OrganizationPatchCommand> = JsonNullable.undefined(),

    @JsonProperty("patient")
    val patient: JsonNullable<@Valid PatientPatchCommand> = JsonNullable.undefined(),

    @JsonProperty("sample")
    val sample: JsonNullable<@Valid SamplePatchCommand> = JsonNullable.undefined(),

    @JsonProperty("extensions")
    val extensions: JsonNullable<List<@Valid RequestExtensionCommand>> = JsonNullable.undefined()
)

/**
 * 의뢰 검색 Query
 */
data class SearchRequestQuery(
    @field:NotNull(message = "{validation.notNull}")
    @field:PastOrPresent(message = "{validation.pastOrPresent}")
    @JsonProperty("request_date_from")
    val requestDateFrom: LocalDate,

    @field:NotNull(message = "{validation.notNull}")
    @field:PastOrPresent(message = "{validation.pastOrPresent}")
    @JsonProperty("request_date_to")
    val requestDateTo: LocalDate,

    @JsonProperty("sample")
    @field:Valid
    val sample: SampleFilter? = null,

    @JsonProperty("service")
    @field:Valid
    val service: ServiceFilter? = null,

    @JsonProperty("organization")
    @field:Valid
    val organization: OrganizationFilter? = null,

    @JsonProperty("patient")
    @field:Valid
    val patient: PatientFilter? = null
)

// ============================================
// 필터 DTO
// ============================================

data class SampleFilter(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String
)

data class ServiceFilter(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String
)

data class OrganizationFilter(
    @field:NotBlank(message = "{validation.notBlank}")
    val serial: String
)

data class PatientFilter(
    @JsonProperty("serial")
    val serial: String? = null,

    @JsonProperty("name")
    val name: String? = null
)
