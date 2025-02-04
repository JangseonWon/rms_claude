package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class RequestRelationDTO(
    @JsonProperty("id")
    var id: Int? = null,
    @JsonProperty("name")
    var name: String? = null
)
