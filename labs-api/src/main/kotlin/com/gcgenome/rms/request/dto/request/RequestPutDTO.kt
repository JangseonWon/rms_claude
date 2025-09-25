package com.gcgenome.rms.request.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class RequestPutDTO(
    val department: String?,
    val ward: String?,
    val physician: String?,
    val memo: String?,
    @JsonProperty("genome_price") val genomePrice: Long?,
    @JsonProperty("labs_price")   val labsPrice: Long?,
    @field:Valid val organization: OrganizationRefDTO,
    @field:Valid val sample: SampleRefDTO,
    @field:Valid val patient: PatientRefDTO,
    val extensions: List<RequestExtensionRefDTO>? = null
)
data class SampleTypeRefDTO(
    @field:NotBlank(message = "must not be blank")
    val serial: String
)

data class SampleRefDTO(
    @field:NotNull(message = "must not be null")
    @field:Positive(message = "must be > 0")
    val count: Int,

    @JsonProperty("sampling_on")
    @field:NotNull(message = "must not be null")
    @field:PastOrPresent(message = "must be past or today")
    val samplingOn: LocalDate,

    @NotNull
    @field:Valid
    val type: SampleTypeRefDTO
)

data class OrganizationRefDTO(
    @field:NotBlank(message = "must not be blank") val serial: String
)
data class PatientRefDTO (
    @field:NotBlank(message = "must not be blank")
    val serial: String,
    @field:NotBlank(message = "must not be blank")
    val name: String,
    val sex: String? = null,
    val age: Int? = null,
    val birth: LocalDate? = null
)

data class RequestExtensionRefDTO(
    @field:NotBlank(message = "must not be blank")
    val code: String,
    val value: String
)