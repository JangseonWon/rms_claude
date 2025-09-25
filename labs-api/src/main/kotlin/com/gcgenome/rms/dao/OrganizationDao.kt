package com.gcgenome.rms.dao

import com.gcgenome.rms.entity.OrganizationEntity
import com.gcgenome.rms.organization.dto.request.OrganizationPatchDTO
import com.gcgenome.rms.organization.dto.request.OrganizationPostDTO
import com.gcgenome.rms.organization.dto.response.OrganizationResponseDTO
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import org.jooq.Field
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface OrganizationDao {
    fun DSLContext.selectOrganizationByUserIdAndSerial(userId: UUID, serial: String): Mono<OrganizationResponseDTO> {
        return Mono.from(
            selectFrom(ORGANIZATION).where(
                ORGANIZATION.USER_ID.eq(userId),
                ORGANIZATION.SERIAL.eq(serial)
            )
        ).map { it.into(OrganizationResponseDTO::class.java) }
    }
    fun DSLContext.searchOrganizations(userId: UUID, organization: OrganizationPostDTO): Flux<OrganizationResponseDTO> {
        return Flux.from(
            selectFrom(ORGANIZATION)
                .where(
                    listOfNotNull(
                        ORGANIZATION.USER_ID.eq(userId),
                        organization.serial?.let { ORGANIZATION.SERIAL.like("%$it%") },
                        organization.name?.let { ORGANIZATION.NAME.like("%$it%") },
                        organization.registrationNumber?.let { ORGANIZATION.REGISTRATION_NUMBER.like("%$it%") },
                        organization.nursingNumber?.let { ORGANIZATION.NURSING_NUMBER.like("%$it%") },
                        organization.branchCode?.let { ORGANIZATION.BRANCH_CODE.like("%$it%") },
                        organization.branchName?.let { ORGANIZATION.BRANCH_NAME.like("%$it%") },
                        organization.employeeId?.let { ORGANIZATION.EMPLOYEE_ID.like("%$it%") },
                        organization.employeeName?.let { ORGANIZATION.EMPLOYEE_NAME.like("%$it%") },
                        organization.employeePhone?.let { ORGANIZATION.EMPLOYEE_PHONE.like("%$it%") },
                        organization.type?.let { ORGANIZATION.TYPE.like("%$it%") }
                    )
                )
        ).map { it.into(OrganizationResponseDTO::class.java) }
    }
    fun DSLContext.insertOrganization(organization: OrganizationEntity): Mono<OrganizationResponseDTO> {
        return Mono.from(
            insertInto(ORGANIZATION)
                .set(ORGANIZATION.ID, organization.id)
                .set(ORGANIZATION.SERIAL, organization.serial)
                .set(ORGANIZATION.NAME, organization.name)
                .set(ORGANIZATION.REGISTRATION_NUMBER, organization.registrationNumber)
                .set(ORGANIZATION.NURSING_NUMBER, organization.nursingNumber)
                .set(ORGANIZATION.BRANCH_CODE, organization.branchCode)
                .set(ORGANIZATION.BRANCH_NAME, organization.branchName)
                .set(ORGANIZATION.EMPLOYEE_ID, organization.employeeId)
                .set(ORGANIZATION.EMPLOYEE_NAME, organization.employeeName)
                .set(ORGANIZATION.EMPLOYEE_PHONE, organization.employeePhone)
                .set(ORGANIZATION.TYPE, organization.type)
                .set(ORGANIZATION.CREATE_AT, organization.createAt)
                .set(ORGANIZATION.USER_ID, organization.userId)
                .returning()
        ).flatMap { rec ->
            selectOrganizationByUserIdAndSerial(organization.userId, organization.serial)
        }
    }
    fun DSLContext.deleteOrganizationBySerial(userId: UUID, serial: String): Mono<Int> {
        return Mono.from(
            deleteFrom(ORGANIZATION).where(
                ORGANIZATION.USER_ID.eq(userId),
                ORGANIZATION.SERIAL.eq(serial)
            )
        )
    }

    fun DSLContext.updateOrganizationById(userId: UUID, organizationSerial: String, patch: OrganizationPatchDTO): Mono<OrganizationResponseDTO> {

        val updates = mutableMapOf<Field<*>, Any?>()//

        if (patch.name.isPresent)                 updates[ORGANIZATION.NAME]                   = patch.name.orElse(null)
        if (patch.registrationNumber.isPresent)   updates[ORGANIZATION.REGISTRATION_NUMBER]    = patch.registrationNumber.orElse(null)
        if (patch.nursingNumber.isPresent)        updates[ORGANIZATION.NURSING_NUMBER]         = patch.nursingNumber.orElse(null)
        if (patch.branchCode.isPresent)           updates[ORGANIZATION.BRANCH_CODE]            = patch.branchCode.orElse(null)
        if (patch.branchName.isPresent)           updates[ORGANIZATION.BRANCH_NAME]            = patch.branchName.orElse(null)
        if (patch.employeeId.isPresent)           updates[ORGANIZATION.EMPLOYEE_ID]            = patch.employeeId.orElse(null)
        if (patch.employeeName.isPresent)         updates[ORGANIZATION.EMPLOYEE_NAME]          = patch.employeeName.orElse(null)
        if (patch.employeePhone.isPresent)        updates[ORGANIZATION.EMPLOYEE_PHONE]         = patch.employeePhone.orElse(null)
        if (patch.type.isPresent)                 updates[ORGANIZATION.TYPE]                   = patch.type.orElse(null)

        if (updates.isEmpty()) {
            return selectOrganizationByUserIdAndSerial(userId, organizationSerial)
        }

        return Mono.from(update(ORGANIZATION).set(updates)
            .where(
            ORGANIZATION.USER_ID.eq(userId),
            ORGANIZATION.SERIAL.eq(organizationSerial)
        ).returning()
        ).map { it.into(OrganizationResponseDTO::class.java) }
    }
}