package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class UserDTO(
    var id: String? = null,
    var name: String? = null,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null,
    var role: String? = null,
    var type: String? = null,
    var email: String? = null,
    @JsonProperty("phone_number")
    var phoneNumber: String? = null,
    var key: UUID? = null,
    var state: String? = null,
    @JsonProperty("branch_serial")
    var branchSerial:String? = null,
    @JsonProperty("branch_name")
    var branchName:String? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime? = null,
    var services: Array<ServiceDTO>? = emptyArray(),
    var organizations: Array<OrganizationDTO>? = emptyArray()
)
