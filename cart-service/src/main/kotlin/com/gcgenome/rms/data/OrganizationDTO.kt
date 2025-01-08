package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class OrganizationDTO(
    @JsonProperty("id")
    val id:String,
    @JsonProperty("user")
    val user: UserDTO?,
    @JsonProperty("name")
    val name:String?,
    @JsonProperty("registration_number")
    val registrationNumber:String?,
    @JsonProperty("type")
    val type:String?,
    @JsonProperty("nursing_number")
    val nursingNumber:String?,
)