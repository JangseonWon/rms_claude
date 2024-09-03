package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

class AlisExtension (
    @JsonProperty("custom_code")
    val customCode: String,
    @JsonProperty("custom_display_name")
    val customDisplayName: String
)