package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Service(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("sample_types")
    val sampleTypes: Array<SampleType>?,
    @JsonProperty("extensions")
    val extensions: Array<Extension>?
)