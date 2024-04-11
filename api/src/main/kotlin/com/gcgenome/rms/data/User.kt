package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
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
    val branchName: String?,
    @JsonProperty("create_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    val createAt: LocalDateTime?
)
