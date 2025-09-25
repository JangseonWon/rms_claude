package com.gcgenome.rms.organization.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class OrganizationPostDTO(
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") val serial: String? = null,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("registration_number") val registrationNumber: String? = null,
    @JsonProperty("nursing_number") val nursingNumber: String? = null,
    @JsonProperty("branch_code") val branchCode: String? = null,
    @JsonProperty("branch_name") val branchName: String? = null,
    @JsonProperty("employee_id")  val employeeId: String? = null,
    @JsonProperty("employee_name") val employeeName: String? = null,
    @JsonProperty("employee_phone") val employeePhone: String? = null,
    @JsonProperty("type") val type: String? = null
)
