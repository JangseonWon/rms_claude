package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceDTO(
    var id: String? = null,
    var name: String? = null,
    @JsonProperty("name_kr")
    var nameKr: String? = null,
    @JsonProperty("group_name")
    var groupName: String? = null,
    var type: String? = null,
    var category: Category? = null,
    var extensions: List<ExtensionDTO>? = null,
    @JsonProperty("sample_types")
    var sampleTypes: List<SampleTypeDTO>? = null
)
