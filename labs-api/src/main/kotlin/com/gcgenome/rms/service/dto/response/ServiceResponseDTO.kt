package com.gcgenome.rms.service.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class ServiceResponseDTO(
    @JsonProperty("code") val code: String,
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
    @JsonProperty("serial") val serial: String,
    @JsonProperty("count") val count: String,
    @JsonProperty("sampling_on") val samplingOn: LocalDate,
    @JsonProperty("create_at") val createAt: LocalDateTime? = null,
    @JsonProperty("sample_type_id") val sampleTypeId: String,
    @JsonProperty("user_id") val userId: String,
    @JsonProperty("type") var type: SampleTypeServiceResponseDTO
)

data class SampleTypeServiceResponseDTO(
    @JsonIgnore val id: UUID,
    @JsonProperty("code") val code: String,
    @JsonProperty("serial") val serial: String,
    @JsonProperty("name_kr") val nameKr: String? = null,
    @JsonProperty("name_en") val nameEn: String? = null,
)

data class ExtensionResponseDTO(
    @JsonIgnore val id: UUID,
    @JsonProperty("code") val code: String,
    @JsonProperty("name_kr") val nameKr: String? = null,
    @JsonProperty("name_en") val nameEn: String? = null,
    @JsonProperty("is_required") val isRequired: Boolean,
    @JsonProperty("regex") val regex: String? = null
)
