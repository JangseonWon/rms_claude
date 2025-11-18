package com.idrsys.ailis.rms.application.mapper

import com.idrsys.ailis.rms.application.dto.request.CreateOrganizationCommand
import com.idrsys.ailis.rms.application.dto.response.OrganizationListResponse
import com.idrsys.ailis.rms.application.dto.response.OrganizationResponse
import com.idrsys.ailis.rms.domain.model.Organization

/**
 * Organization Mapper
 *
 * Organization Domain 모델과 DTO 간의 변환을 담당합니다.
 */
object OrganizationMapper {

    /**
     * CreateOrganizationCommand → Organization Domain
     */
    fun toDomain(command: CreateOrganizationCommand, userId: String): Organization {
        return Organization.create(
            serial = command.serial,
            name = command.name,
            registrationNumber = command.registrationNumber,
            nursingNumber = command.nursingNumber,
            branchCode = command.branchCode,
            branchName = command.branchName,
            employeeId = command.employeeId,
            employeeName = command.employeeName,
            employeePhone = command.employeePhone,
            type = command.type,
            createdBy = userId
        )
    }

    /**
     * Organization Domain → OrganizationResponse
     */
    fun toResponse(organization: Organization): OrganizationResponse {
        return OrganizationResponse(
            id = organization.id!!,
            serial = organization.serial,
            name = organization.name,
            registrationNumber = organization.registrationNumber,
            nursingNumber = organization.nursingNumber,
            branchCode = organization.branchCode,
            branchName = organization.branchName,
            employeeId = organization.employeeId,
            employeeName = organization.employeeName,
            employeePhone = organization.employeePhone,
            type = organization.type,
            createdAt = organization.createdAt,
            createdBy = organization.createdBy,
            updatedAt = organization.updatedAt,
            updatedBy = organization.updatedBy
        )
    }

    /**
     * Organization Domain → OrganizationListResponse
     */
    fun toListResponse(organization: Organization): OrganizationListResponse {
        return OrganizationListResponse(
            id = organization.id!!,
            serial = organization.serial,
            name = organization.name,
            type = organization.type,
            createdAt = organization.createdAt
        )
    }
}
