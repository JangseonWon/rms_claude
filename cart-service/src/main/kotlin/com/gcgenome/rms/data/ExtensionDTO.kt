package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

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
    @JsonProperty("varue")
    var varue: String?
)
