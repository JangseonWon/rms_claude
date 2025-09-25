package com.gcgenome.rms.organization.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.*

data class OrganizationResponseDTO(
    @JsonIgnore
    val id: UUID,
    @field:NotBlank(message = "must not be blank")
    @JsonProperty("serial") val serial: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("registration_number") val registrationNumber: String? = null,
    @JsonProperty("nursing_number") val nursingNumber: String? = null,
    @JsonProperty("branch_code") val branchCode: String? = null,
    @JsonProperty("branch_name") val branchName: String? = null,
    @JsonProperty("employee_id") val employeeId: String? = null,
    @JsonProperty("employee_name") val employeeName: String? = null,
    @JsonProperty("employee_phone") val employeePhone: String? = null,
    @JsonProperty("type") val type: String? = null,
    @JsonProperty("create_at") val createAt: LocalDateTime? = null,
    @JsonIgnore
    val userId: UUID? = null,
)
