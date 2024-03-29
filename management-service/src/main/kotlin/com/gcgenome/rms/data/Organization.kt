package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Organization(
    @JsonProperty("id")
    val id:String,
    @JsonProperty("user_id")
    val userId:String?,
    @JsonProperty("name")
    val name:String?,
    @JsonProperty("type")
    val type:String?,
    @JsonProperty("registration_number")
    val registrationNumber:String?,
    @JsonProperty("nursing_number")
    val nursingNumber:String?
)