package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Extension(
    @JsonProperty("id")
    val id: String?,
    @JsonProperty("sample_id")
    val sampleId: UUID?,
    @JsonProperty("value")
    val value: String?
)
