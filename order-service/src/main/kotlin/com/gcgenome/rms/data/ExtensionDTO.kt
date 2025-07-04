package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ExtensionDTO(
    @JsonProperty("id")
    var id: String?,
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("name_kr")
    var nameKr: String?,
    @JsonProperty("regex")
    var regex: String?,
    @JsonProperty("type")
    var type: String?,
    @JsonProperty("value")
    var value: String?,
    @JsonProperty("required")
    var required: Boolean?,
    @JsonProperty("sort_extension")
    var sortExtension: Int?

)
