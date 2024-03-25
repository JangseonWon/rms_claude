package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class User(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("password")
    val password: String?,
    @JsonProperty("role")
    val role: String,
    @JsonProperty("type")
    val type: String?,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("phone_number")
    val phoneNumber: String?,
    @JsonProperty("key")
    val key: UUID?,
    @JsonProperty("state")
    val state: String?,
    @JsonProperty("branch_serial")
    val branchSerial: String?,
    @JsonProperty("branch_name")
    val branchName: String?
)
