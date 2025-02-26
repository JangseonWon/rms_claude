package com.gcgenome.rms.authentication

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class User(
    @JsonProperty("id")
    val id: String?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("phone_number")
    val phoneNumber: String?,
    @JsonProperty("branch_name")
    val branchName: String?,
    @JsonProperty("branch_serial")
    val branchSerial: String?,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("role")
    val role: String?,
    @JsonProperty("state")
    val state: String?,
    @JsonProperty("type")
    val type: String?,
    @JsonProperty("key")
    val key: String?,
    @JsonProperty("employee_department")
    val employeeDepartment: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("last_password_changed_at")
    val lastPasswordChangedAt: LocalDateTime?

)
