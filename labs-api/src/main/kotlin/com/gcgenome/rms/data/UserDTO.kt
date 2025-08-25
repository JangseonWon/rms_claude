package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class UserDTO(
    @JsonProperty("id")
    var id: UUID,
    @JsonProperty("login_id")
    var loginId: String? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("password")
    @JsonIgnore
    var password: String? = null,
    @JsonProperty("role")
    var role: String? = null,
    @JsonProperty("email")
    var email: String? = null,
    @JsonProperty("key")
    var key: UUID? = null,
    @JsonProperty("state")
    var state: String? = null,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    var createAt: LocalDateTime? = null
)
