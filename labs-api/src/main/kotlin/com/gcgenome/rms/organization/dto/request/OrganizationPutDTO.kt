package com.gcgenome.rms.organization.dto.request

import jakarta.validation.constraints.NotBlank


data class OrganizationPutDTO(
    @field:NotBlank(message = "must not be blank")
    val name: String,
    val registrationNumber: String? = null,
    val nursingNumber: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val employeePhone: String? = null,
    val type: String? = null
)
