package com.gcgenome.rms.profile.organization

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OrganizationHandler(
    val dslContext: DSLContext,
): OrganizationDao {


    fun insertOrganization(organization: OrganizationDTO): Mono<OrganizationDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run { insertOrganization(organization) }
        })
    }

    fun updateOrganization(organization: OrganizationDTO): Mono<OrganizationDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run { updateOrganization(organization) }
        })
    }

    fun selectUserWithOrganizations(userId: String, query: Query):  Mono<Page<OrganizationDTO>> {
        return dslContext.selectUserWithOrganizations(userId, query)
    }
}