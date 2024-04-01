package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class User(
    val id: String,
    val name: String?,
    val password: String?,
    val role: String,
    val type: String?,
    val email: String?,
    @JsonProperty("phone_number")
    val phoneNumber: String?,
    val key: UUID?,
    val state: String?,
    @JsonProperty("branch_serial")
    val branchSerial:String?,
    @JsonProperty("branch_name")
    val branchName:String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    var organization: Organization?
)
