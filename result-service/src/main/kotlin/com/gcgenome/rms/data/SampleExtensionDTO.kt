package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID


data class SampleExtensionDTO(
    @JsonProperty("sample_id")
    var sampleId: UUID? = null,
    @JsonProperty("extension_id")
    var extensionId: String? = null,
    var value: String? = null,

)
