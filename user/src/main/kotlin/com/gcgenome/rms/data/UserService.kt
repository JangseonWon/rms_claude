package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty


data class UserService (
    @JsonProperty("user_id")
    val userId: String?,
    @JsonProperty("service_id")
    val serviceId: String?
)