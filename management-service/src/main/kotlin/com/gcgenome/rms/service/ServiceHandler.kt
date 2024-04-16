package com.gcgenome.rms.service

import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.ServiceExtensionDao
import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.pojos.Service
import com.gcgenome.rms.tables.pojos.ServiceExtension
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class ServiceHandler(
    val dslContext: DSLContext
): ServiceDao, ServiceExtensionDao {

    fun selectServiceByNameOrId(filter: Query.Companion.Filter): Flux<Service> {
        val whereClause = buildServiceIdOrNameWhereClause(filter)
        return Flux.from(dslContext.selectServiceByNameOrId(whereClause))
    }

    fun selectServiceExtensions(filter: Query.Companion.Filter, serviceId: String): Flux<Extension> {
        val whereClause = buildExtensionIdOrNameWhereClause(filter)
        return Flux.from(dslContext.selectServiceExtensionByNameOrId(whereClause, serviceId))
    }

    fun buildServiceIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("service.id").like("%${filter.value}%").or(field("service.name").like("%${filter.value}%"))
    }

    fun buildExtensionIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("extension.id").like("%${filter.value}%").or(field("extension.name").like("%${filter.value}%"))
    }
}