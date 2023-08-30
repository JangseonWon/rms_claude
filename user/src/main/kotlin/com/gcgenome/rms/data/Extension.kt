package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Extension(
    @JsonProperty("id")
    val id: String?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("required")
    val required: Boolean?,
    @JsonProperty("regex")
    val regex: String?,
)
