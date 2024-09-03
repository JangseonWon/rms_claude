package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

class AlisService (
    @JsonProperty("test_code")
    val testCode: String,
    @JsonProperty("test_display_name")
    val testDisplayName: String
)