package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class RequestExtensionDTO(
    @JsonProperty("id")
    var id: UUID? = null,
    @JsonProperty("value")
    var value: String? = null,
    @JsonProperty("code")
    var code: String? = null,
    @JsonProperty("name_kr")
    var nameKr: String? = null,
    @JsonProperty("name_en")
    var nameEn: String? = null,

    var extensionId: UUID? = null,
    var requestId: UUID? = null,
)
