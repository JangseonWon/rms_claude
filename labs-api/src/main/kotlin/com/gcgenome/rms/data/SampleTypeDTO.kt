package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class SampleTypeDTO(
    @JsonProperty("id")
    var id: UUID? = null,
    @JsonProperty("code")
    var code: String? = null,
    @JsonProperty("serial")
    var serial: String? = null,
    @JsonProperty("name_kr")
    var nameKr: String? = null,
    @JsonProperty("name_en")
    var nameEn: String? = null
)
