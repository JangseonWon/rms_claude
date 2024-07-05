package com.gcgenome.rms.service

import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.ServiceExtensionDao
import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.tables.pojos.SampleType
import com.gcgenome.rms.tables.pojos.Service
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ServiceHandler(
    val dslContext: DSLContext
): ServiceDao, ServiceExtensionDao, SampleTypeDao {

    fun checkServiceById(serviceId: String): Mono<Service_> {
        return dslContext.selectServiceById(serviceId)
            .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
    }

    fun updateServiceById(service: Service): Mono<Service> {
        return checkServiceById(service.id!!).flatMap {
            dslContext.updateServiceById(service)
        }
    }

    fun selectServiceByNameOrId(filter: Query.Companion.Filter): Flux<Service> {
        val whereClause = buildServiceIdOrNameWhereClause(filter)
        return Flux.from(dslContext.selectServiceByNameOrId(whereClause))
    }

    fun selectServiceByUserId(userId: String): Flux<Service_> {
        return Flux.from(dslContext.selectServiceByUserId(userId))
    }

    fun selectServiceExtensions(filter: Query.Companion.Filter, serviceId: String): Flux<Extension> {
        val whereClause = buildExtensionIdOrNameWhereClause(filter)
        return Flux.from(dslContext.run {
            selectServiceById(serviceId).switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
                .thenMany(selectServiceExtensionByNameOrId(whereClause, serviceId))
        })
    }

    fun selectSampleTypes(filter: Query.Companion.Filter, serviceId: String): Flux<SampleType> {
        return Flux.from(dslContext.run {
            selectServiceById(serviceId).switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
                .thenMany(selectSampleTypeByNameOrId(serviceId, filter.value))
        })
    }

    fun buildServiceIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("service.id").like("%${filter.value}%").or(field("service.name").like("%${filter.value}%"))
    }

    fun buildExtensionIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("extension.id").like("%${filter.value}%").or(field("extension.name").like("%${filter.value}%"))
    }
}