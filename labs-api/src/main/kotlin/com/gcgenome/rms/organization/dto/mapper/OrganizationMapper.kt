package com.gcgenome.rms.organization.dto.mapper

import com.gcgenome.rms.entity.OrganizationEntity
import com.gcgenome.rms.organization.dto.request.OrganizationPostDTO
import com.gcgenome.rms.organization.dto.request.OrganizationPutDTO
import java.time.LocalDateTime
import java.util.*

//PUT
fun OrganizationPutDTO.toOrganizationEntity(userId: UUID, serial: String) : OrganizationEntity {
    return OrganizationEntity(
        id = UUID.randomUUID(),
        serial = serial,
        name = this.name,
        registrationNumber = this.registrationNumber,
        nursingNumber = this.nursingNumber,
        branchCode = this.branchCode,
        branchName = this.branchName,
        employeeId = this.employeeId,
        employeeName = this.employeeName,
        employeePhone = this.employeePhone,
        type = this.type,
        createAt = LocalDateTime.now(),
        userId = userId,
    )
}