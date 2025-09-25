package com.gcgenome.rms.service.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class ServiceResponseDTO(
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("code") val code: String,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") var serial: String? = null,
    @JsonProperty("name_kr") val nameKr: String? = null,
    @JsonProperty("name_en") val nameEn: String? = null,
    @JsonProperty("sample_types") val sampleTypes: List<SampleResponseDTO>? = null,
    @JsonProperty("extensions") val extensions: List<ExtensionResponseDTO>? = null,
)

data class ServiceExtensionDTO(
    @JsonProperty("id") val id: String,
    @JsonProperty("is_required") var isRequired: Boolean,
    @JsonProperty("service_id") val serviceId: String,
    @JsonProperty("extenstion_id") val extensionId: String,
    @JsonProperty("extension") val extension: ExtensionResponseDTO
)

data class SampleResponseDTO(
    @JsonProperty("id") val id: UUID,
    @JsonProperty("barcode") val barcode: String? = null,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") val serial: String,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("count") val count: String,

    @field:NotBlank(message = "must not be blank")
    @field:PastOrPresent(message = "must be past or today")
    @JsonProperty("sampling_on") val samplingOn: LocalDate,
    @JsonProperty("create_at") val createAt: LocalDateTime? = null,
    @JsonProperty("sample_type_id") val sampleTypeId: String,
    @JsonProperty("user_id") val userId: String,
    @JsonProperty("type") var type: SampleTypeServiceResponseDTO
)

data class ServiceTypeResponseDTO(
    @JsonIgnore val id: UUID? = null,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("code") var code: String,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") var serial: String? = null,
    @JsonProperty("name_kr") var nameKr: String? = null,
    @JsonProperty("name_en") var nameEn: String? = null,
    @JsonProperty("sample_types") var sampleTypes: List<SampleTypeServiceResponseDTO>? = null,
    @JsonProperty("extensions") var extensions: List<ExtensionResponseDTO>? = null,
)

data class SampleTypeServiceResponseDTO(
    @JsonIgnore val id: UUID,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("code") val code: String,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") val serial: String,
    @JsonProperty("name_kr") val nameKr: String? = null,
    @JsonProperty("name_en") val nameEn: String? = null,
)

data class ExtensionResponseDTO(
    @JsonIgnore val id: UUID,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("code") val code: String,
    @JsonProperty("name_kr") val nameKr: String? = null,
    @JsonProperty("name_en") val nameEn: String? = null,
    @JsonProperty("is_required") val isRequired: Boolean,
    @JsonProperty("regex") val regex: String? = null
)
