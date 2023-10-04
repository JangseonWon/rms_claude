package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class SampleExtension(
    @JsonProperty("extension_id")
    val extensionId: String,
    @JsonProperty("sample_id")
    val sampleId: UUID,
    @JsonProperty("value")
    val value: String?
)
