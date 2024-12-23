package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty

data class OrganizationDTO(
    @JsonProperty("id")
    val id:String,
    @JsonProperty("name")
    val name:String?,
    @JsonProperty("type")
    val type:String?,
    @JsonProperty("registration_number")
    val registrationNumber:String?,
    @JsonProperty("nursing_number")
    val nursingNumber:String?,
    @JsonProperty("branch_code")
    val branchCode:String?,
    @JsonProperty("branch_name")
    val branchName:String?,
    @JsonProperty("user")
    val user: User?,
)