package com.gcgenome.rms.organization.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import org.openapitools.jackson.nullable.JsonNullable


data class OrganizationPatchDTO(
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("name") val name:  JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("registration_number")  val registrationNumber: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("nursing_number") val nursingNumber: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("branch_code") val branchCode: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("branch_name") val branchName: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("employee_id") val employeeId: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("employee_name") val employeeName: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("employee_phone") val employeePhone: JsonNullable<String> = JsonNullable.undefined(),
    @JsonProperty("type") val type: JsonNullable<String> = JsonNullable.undefined()
)
