package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class UserDTO(
    @JsonProperty("id")
    var id: String?,
    @JsonProperty("name")
    var name: String?,
    @JsonProperty("role")
    var role: Role?,
    @JsonProperty("type")
    var type: String?,
    @JsonProperty("email")
    var email: String?,
    @JsonProperty("phone_number")
    var phoneNumber: String?,
    @JsonProperty("key")
    var key: UUID?,
    @JsonProperty("state")
    var state: String?,
    @JsonProperty("branch_serial")
    var branchSerial: String?,
    @JsonProperty("branch_name")
    var branchName: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime?
)
