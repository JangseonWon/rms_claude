package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ServiceDTO(
    @JsonIgnore
    var id: UUID? = null,
    @JsonProperty("code")
    var code: String? = null,
    @JsonProperty("serial")
    var serial: String? = null,
    @JsonProperty("name_kr")
    var nameKr: String? = null,
    @JsonProperty("name_en")
    var nameEn: String? = null,
    @JsonProperty("type")
    var type: String? = null,

    @JsonProperty("sample_types")
    var sampleTypes: List<SampleTypeDTO>? = null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("extensions")
    var extensions: List<ExtensionDTO>? = emptyList()
)
