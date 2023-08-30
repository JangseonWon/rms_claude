package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class User(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("authority")
    val authority: String,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("key")
    val key: UUID?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("state")
    val state: String?,
    @JsonProperty("code")
    val code: Short?,
    @JsonProperty("password")
    val password: String?,
    @JsonProperty("organization")
    val organization: Organization?,
)
