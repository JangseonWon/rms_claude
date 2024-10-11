package com.gcgenome.rms.profile.organization

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.data.OrganizationDTO
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import org.jooq.impl.DSL
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

    fun selectUserWithOrganizations(user: UserAuthentication, query: Query):  Mono<Page<OrganizationDTO>> {
        val where = if (user.user.role == "USER") ORGANIZATION.USER_ID.eq(user.user.id) else DSL.noCondition()
        return dslContext.selectUserWithOrganizations(where, query)
    }
}