package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Organization_(
    val id:String,
    @JsonProperty("user_id")
    val userId:String?,
    val name:String?,
    val type:String?,
    @JsonProperty("registration_number")
    val registrationNumber:String?,
    @JsonProperty("nursing_number")
    val nursingNumber:String?
)