package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class User(
    var id: String,
    var name: String?,
    var password: String?,
    var role: String?,
    var type: String?,
    var email: String?,
    @JsonProperty("phone_number")
    var phoneNumber: String?,
    var key: UUID?,
    var state: String?,
    @JsonProperty("branch_serial")
    var branchSerial:String?,
    @JsonProperty("branch_name")
    var branchName:String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime?,
    var organization: Organization_?,
    var service:Service_?
)
