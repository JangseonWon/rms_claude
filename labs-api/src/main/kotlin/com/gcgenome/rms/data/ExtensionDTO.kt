package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ExtensionDTO(
    @JsonProperty("id")
    var id: UUID? = null,
    @JsonProperty("code")
    var code: String? = null,
    @JsonProperty("value")
    var value: String? = null,
    @JsonProperty("name_kr")
    var nameKr: String? = null,
    @JsonProperty("name_en")
    var nameEn: String? = null,
    @JsonProperty("regex")
    var regex: String? = null,
    @JsonProperty("is_required")
    var isRequired: Boolean? = null
)
