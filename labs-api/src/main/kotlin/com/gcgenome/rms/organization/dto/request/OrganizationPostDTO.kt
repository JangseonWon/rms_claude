package com.gcgenome.rms.organization.dto.request

data class OrganizationPostDTO(
    val serial: String? = null,
    val name: String? = null,
    val registrationNumber: String? = null,
    val nursingNumber: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val employeePhone: String? = null,
    val type: String? = null
)
