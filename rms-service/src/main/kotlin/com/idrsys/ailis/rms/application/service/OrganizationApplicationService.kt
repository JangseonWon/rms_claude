package com.idrsys.ailis.rms.application.service

import com.idrsys.ailis.rms.application.dto.request.CreateOrganizationCommand
import com.idrsys.ailis.rms.application.dto.request.PatchOrganizationCommand
import com.idrsys.ailis.rms.application.dto.request.UpdateOrganizationCommand
import com.idrsys.ailis.rms.application.dto.response.OrganizationListResponse
import com.idrsys.ailis.rms.application.dto.response.OrganizationResponse
import com.idrsys.ailis.rms.application.mapper.OrganizationMapper
import com.idrsys.ailis.rms.application.usecase.OrganizationUseCase
import com.idrsys.ailis.rms.domain.repository.OrganizationRepository
import com.idrsys.ailis.rms.shared.exception.DuplicateOrganizationException
import com.idrsys.ailis.rms.shared.exception.OrganizationHasRequestException
import com.idrsys.ailis.rms.shared.exception.OrganizationNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 기관 관리 Application Service
 */
@Service
@Transactional
class OrganizationApplicationService(
    private val organizationRepository: OrganizationRepository
) : OrganizationUseCase {

    override suspend fun createOrganization(command: CreateOrganizationCommand, userId: String): OrganizationResponse {
        // 중복 체크
        if (organizationRepository.existsBySerial(command.serial)) {
            throw DuplicateOrganizationException(command.serial)
        }

        val organization = organizationRepository.save(
            OrganizationMapper.toDomain(command, userId)
        )

        return OrganizationMapper.toResponse(organization)
    }

    @Transactional(readOnly = true)
    override suspend fun getOrganization(id: Long): OrganizationResponse {
        val organization = organizationRepository.findById(id)
            ?: throw OrganizationNotFoundException(id.toString())

        return OrganizationMapper.toResponse(organization)
    }

    @Transactional(readOnly = true)
    override suspend fun getOrganizationBySerial(serial: String): OrganizationResponse {
        val organization = organizationRepository.findBySerial(serial)
            ?: throw OrganizationNotFoundException.bySerial(serial)

        return OrganizationMapper.toResponse(organization)
    }

    @Transactional(readOnly = true)
    override fun getAllOrganizations(): Flow<OrganizationListResponse> {
        return organizationRepository.findAll()
            .map { OrganizationMapper.toListResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun searchOrganizationsByName(name: String): Flow<OrganizationListResponse> {
        return organizationRepository.findByName(name)
            .map { OrganizationMapper.toListResponse(it) }
    }

    override suspend fun updateOrganization(
        id: Long,
        command: UpdateOrganizationCommand,
        userId: String
    ): OrganizationResponse {
        val organization = organizationRepository.findById(id)
            ?: throw OrganizationNotFoundException(id.toString())

        val updatedOrganization = organization.update(
            name = command.name,
            registrationNumber = command.registrationNumber,
            nursingNumber = command.nursingNumber,
            branchCode = command.branchCode,
            branchName = command.branchName,
            employeeId = command.employeeId,
            employeeName = command.employeeName,
            employeePhone = command.employeePhone,
            type = command.type,
            updatedBy = userId
        )

        val saved = organizationRepository.save(updatedOrganization)
        return OrganizationMapper.toResponse(saved)
    }

    override suspend fun patchOrganization(
        id: Long,
        command: PatchOrganizationCommand,
        userId: String
    ): OrganizationResponse {
        val organization = organizationRepository.findById(id)
            ?: throw OrganizationNotFoundException(id.toString())

        val updatedOrganization = organization.copy(
            name = if (command.name.isPresent) command.name.get() else organization.name,
            registrationNumber = if (command.registrationNumber.isPresent) command.registrationNumber.get() else organization.registrationNumber,
            nursingNumber = if (command.nursingNumber.isPresent) command.nursingNumber.get() else organization.nursingNumber,
            branchCode = if (command.branchCode.isPresent) command.branchCode.get() else organization.branchCode,
            branchName = if (command.branchName.isPresent) command.branchName.get() else organization.branchName,
            employeeId = if (command.employeeId.isPresent) command.employeeId.get() else organization.employeeId,
            employeeName = if (command.employeeName.isPresent) command.employeeName.get() else organization.employeeName,
            employeePhone = if (command.employeePhone.isPresent) command.employeePhone.get() else organization.employeePhone,
            type = if (command.type.isPresent) command.type.get() else organization.type
        )

        val saved = organizationRepository.save(updatedOrganization)
        return OrganizationMapper.toResponse(saved)
    }

    override suspend fun deleteOrganization(id: Long): Boolean {
        val organization = organizationRepository.findById(id)
            ?: throw OrganizationNotFoundException(id.toString())

        // 의뢰가 있는지 확인
        if (organizationRepository.hasRequests(id)) {
            throw OrganizationHasRequestException()
        }

        return organizationRepository.deleteById(id)
    }
}
