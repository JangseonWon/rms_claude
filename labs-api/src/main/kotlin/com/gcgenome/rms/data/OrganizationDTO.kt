package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class OrganizationDTO(
    @JsonProperty("id", access = JsonProperty.Access.WRITE_ONLY)
    var id:UUID? = null,
    @JsonProperty("serial")
    var serial:String? = null,
    @JsonProperty("name")
    var name:String? = null,
    @JsonProperty("registration_number")
    var registrationNumber:String? = null,
    @JsonProperty("nursing_number")
    var nursingNumber:String? = null,
    @JsonProperty("branch_code")
    var branchCode:String? = null,
    @JsonProperty("branch_name")
    var branchName:String? = null,
    @JsonProperty("employee_id")
    var employeeId:String? = null,
    @JsonProperty("employee_name")
    var employeeName:String? = null,
    @JsonProperty("employee_phone")
    var employeePhone:String? = null,
    @JsonProperty("type")
    var type:String? = null,
    @JsonProperty("create_at")
    var createAt:LocalDateTime? = null,
    @JsonProperty("user_id", access = JsonProperty.Access.WRITE_ONLY)
    var userId:UUID? = null
)