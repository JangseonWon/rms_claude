package com.gcgenome.rms.organization.handler

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.data.FieldError
import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.exception.ErrorCode
import com.gcgenome.rms.exception.mapUniqueViolation
import com.gcgenome.rms.exception.orNotFound
import com.gcgenome.rms.organization.dto.mapper.toOrganizationEntity
import com.gcgenome.rms.organization.dto.request.OrganizationPostDTO
import com.gcgenome.rms.organization.dto.request.OrganizationPutDTO
import com.gcgenome.rms.organization.dto.response.OrganizationResponseDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class OrganizationHandler(
    private val dsl: DSLContext
): OrganizationDao  {
    fun saveOrganization(userId: UUID, organizationDTO: OrganizationPutDTO, organizationSerial: String): Mono<OrganizationResponseDTO> {
        val organizationEntity = organizationDTO.toOrganizationEntity(userId, organizationSerial)
        return dsl.insertOrganization(organizationEntity)
            .mapUniqueViolation(
                constraint = "uk_organization_user_id_serial",
                fieldErrors = listOf(
                    FieldError(field = "serial", message = "already exists", rejectedValue = organizationSerial)
                ),
                code = ErrorCode.DUPLICATE_KEY
            )
    }

    fun getOrganizationBySerial(userId: UUID, organizationSerial: String): Mono<OrganizationResponseDTO> {
        return dsl.selectOrganizationByUserIdAndSerial(userId, organizationSerial)
            .orNotFound(listOf(FieldError(field = "serial", message = "not found", rejectedValue = organizationSerial)))
    }

    fun searchOrganization(userId: UUID, organizationDTO: OrganizationPostDTO): Flux<OrganizationResponseDTO> {
        return dsl.searchOrganizations(userId, organizationDTO)
    }
    fun deleteOrganizationBySerial(userId: UUID, organizationSerial: String): Mono<OrganizationDTO> {
        return dsl.deleteOrganizationBySerial(userId, organizationSerial)
    }
}