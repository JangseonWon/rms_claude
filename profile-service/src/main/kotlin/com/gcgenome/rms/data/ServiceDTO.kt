package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceDTO(
    var id: String? = null,
    var name: String? = null,
    var category: Category? = null,
    var extensions: List<ExtensionDTO>? = null,
    @JsonProperty("sample_types")
    var sampleTypes: List<SampleTypeDTO>? = null
)
