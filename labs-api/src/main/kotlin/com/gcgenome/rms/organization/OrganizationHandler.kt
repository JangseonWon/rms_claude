package com.gcgenome.rms.organization

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.data.OrganizationDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class OrganizationHandler(
    private val dsl: DSLContext
): OrganizationDao  {
    fun saveOrganization(userId: UUID, organizationDTO: OrganizationDTO, organizationSerial: String): Mono<OrganizationDTO> {
        return dsl.insertOrganization(userId, organizationDTO, organizationSerial)
    }

    fun getOrganizationBySerial(userId: UUID, organizationSerial: String): Mono<OrganizationDTO> {
        return dsl.selectOrganizationByUserIdAndSerial(userId, organizationSerial)
    }

    fun searchOrganization(userId: UUID, organizationDTO: OrganizationDTO): Flux<OrganizationDTO> {
        return dsl.searchOrganizations(userId, organizationDTO)
    }
    fun deleteOrganizationBySerial(userId: UUID, organizationSerial: String): Mono<OrganizationDTO> {
        return dsl.deleteOrganizationBySerial(userId, organizationSerial)
    }
}