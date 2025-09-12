package com.gcgenome.rms.service.dto.response

import com.fasterxml.jackson.annotation.JsonProperty


data class ServiceResponseDTO(
    @JsonProperty("code") val code: String,
    @JsonProperty("serial") val serial: String,
    @JsonProperty("name_kr") val nameKr: String? = null,
    @JsonProperty("name_en") val nameEn: String? = null,
    @JsonProperty("sample_types") val sampleTypes: List<SampleResponseDTO>,
    @JsonProperty("extensions") val extensions: List<ExtensionResponseDTO>? = null
)

data class SampleResponseDTO(
    @JsonProperty("code") val code: String,
    @JsonProperty("serial") val serial: String,
    @JsonProperty("name_kr") val nameKr: String? = null,
)
data class ExtensionResponseDTO(
    @JsonProperty("code") val code: String,
    @JsonProperty("name_kr") val nameKr: String? = null,
    @JsonProperty("name_en") val nameEn: String? = null,
    @JsonProperty("is_required") val isRequired: Boolean,
    @JsonProperty("regex") val regex: String? = null
)
