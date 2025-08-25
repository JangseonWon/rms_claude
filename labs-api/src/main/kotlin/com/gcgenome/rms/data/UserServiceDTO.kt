package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class UserServiceDTO(
    @JsonProperty("id")
    var id: UUID? = null,
    @JsonProperty("serial")
    var serial: String? = null,
    @JsonProperty("create_at")
    var createAt: LocalDateTime? = null,
    @JsonProperty("user_id")
    var userId: UUID? = null,
    @JsonProperty("service_id")
    var serviceId: UUID? = null,
)
