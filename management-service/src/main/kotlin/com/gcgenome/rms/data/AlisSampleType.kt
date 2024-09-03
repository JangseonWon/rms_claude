package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

class AlisSampleType (
    @JsonProperty("sample_code")
    val sampleCode: String,
    @JsonProperty("sample_full_name")
    val sampleFullName: String
)