package com.gcgenome.rms.catalog.organization

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.tables.pojos.Organization
import com.gcgenome.rms.tables.references.ORGANIZATION
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class OrganizationHandler(
    val dslContext: DSLContext,
): OrganizationDao {
    fun getOrganizations(user: UserAuthentication,): Flux<Organization> {
        val andWhere = if (user.user.role == "USER") ORGANIZATION.USER_ID.eq(user.user.id) else DSL.noCondition()
        return dslContext.selectOrganizationsByUserId(andWhere)
    }
}