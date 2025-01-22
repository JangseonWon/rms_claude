package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class RequestGroupDTO(
    @JsonProperty("id")
    val id: UUID?,
)