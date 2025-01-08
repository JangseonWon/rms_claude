package com.gcgenome.rms.product.service

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Service
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
class ServiceHandler(val dslContext: DSLContext):
    OrganizationDao, ServiceDao, SampleTypeDao, RequestDao, PatientDao, SampleDao, SampleExtensionDao
{
    fun getServices(user: UserAuthentication, categoryId: UUID): Flux<Service> {
        val andWhere = if (user.user.role == "USER") USER_SERVICE.USER_ID.eq(user.user.id) else DSL.noCondition()
        return dslContext.selectServiceByUserIdAndCategoryId(andWhere, categoryId)
    }
}