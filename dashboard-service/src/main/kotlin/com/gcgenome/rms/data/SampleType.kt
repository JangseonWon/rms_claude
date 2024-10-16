package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class SampleType(
    @JsonProperty("id")
    var id: String?,
    @JsonProperty("name")
    var name: String?
)