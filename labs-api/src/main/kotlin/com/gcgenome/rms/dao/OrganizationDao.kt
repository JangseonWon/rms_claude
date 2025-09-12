package com.gcgenome.rms.dao

import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.entity.OrganizationEntity
import com.gcgenome.rms.organization.dto.request.OrganizationPostDTO
import com.gcgenome.rms.organization.dto.response.OrganizationResponseDTO
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.UUID

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
        ).map { it.into(OrganizationResponseDTO::class.java) }
    }
    fun DSLContext.deleteOrganizationBySerial(userId: UUID, serial: String): Mono<OrganizationDTO> {
        return Mono.from(
            deleteFrom(ORGANIZATION).where(
                ORGANIZATION.USER_ID.eq(userId),
                ORGANIZATION.SERIAL.eq(serial)
            ).returning()
        ).map { it.into(OrganizationDTO::class.java) }
    }
}