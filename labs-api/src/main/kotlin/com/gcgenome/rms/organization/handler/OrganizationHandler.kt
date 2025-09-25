package com.gcgenome.rms.organization.handler

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.data.FieldError
import com.gcgenome.rms.exception.*
import com.gcgenome.rms.organization.dto.mapper.toOrganizationEntity
import com.gcgenome.rms.organization.dto.request.OrganizationPatchDTO
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
    fun deleteOrganizationBySerial(userId: UUID, organizationSerial: String): Mono<Void> {
        val serial = organizationSerial.trim()

        if (serial.isEmpty()) {
            return Mono.error(
                UnprocessableEntityException(
                    listOf(FieldError("organization.serial", "must not be blank"))
                )
            )
        }
        return dsl.deleteOrganizationBySerial(userId, serial)
            .onErrorMap { _ ->
                ConflictException(
                    code = ErrorCode.DELETE_NOT_ALLOWED,
                    fieldErrors = listOf(FieldError("organization.serial", "in use", serial))
                )
            }
            .flatMap { affected ->
                if (affected == 0) {
                    Mono.error(
                        NotFoundException(
                            listOf(FieldError("organization.serial", "not found", serial))
                        )
                    )
                } else {
                    Mono.empty()
                }
            }
    }

    fun patchOrganization(userId: UUID, organizationSerial: String, patch: OrganizationPatchDTO): Mono<OrganizationResponseDTO> {
        if (patch.name.isPresent && patch.name.orElse(null)?.isBlank() == true)
            return Mono.error(UnprocessableEntityException(listOf(FieldError("organization.name", "must not be blank"))))

        return dsl.updateOrganizationById(userId, organizationSerial, patch)
            .orUnprocessable(
                listOf(FieldError("organization.serial", "not found", organizationSerial))
            )
    }
}