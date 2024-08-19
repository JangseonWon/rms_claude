package com.gcgenome.rms.service

import com.gcgenome.rms.dao.ExtensionDao
import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.ServiceExtensionDao
import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.exception.ExtensionNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.tables.pojos.ServiceExtension
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ServiceExtensionHandler(
    val dslContext: DSLContext
): ServiceDao, ServiceExtensionDao, SampleTypeDao, ExtensionDao {

    fun selectServiceExtensions(filter: Query.Companion.Filter, serviceId: String): Flux<Extension> {
        val whereClause = buildExtensionIdOrNameWhereClause(filter)
        return Flux.from(dslContext.run {
            selectServiceById(serviceId).switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
                .thenMany(selectServiceExtensionByNameOrId(whereClause, serviceId))
        })
    }

    fun selectExtensionAll(filter: Query.Companion.Filter): Flux<Extension> {
        val whereClause = buildExtensionIdOrNameWhereClause(filter)
        return Flux.from(dslContext.selectExtensionByNameOrdId(whereClause))
    }

    fun getExtensionAll(): Flux<com.gcgenome.rms.tables.pojos.Extension> {
        return Flux.from(dslContext.getExtensions())
    }

    fun checkExtensionById(extensionId: String): Mono<com.gcgenome.rms.tables.pojos.Extension> {
        return dslContext.selectExtensionById(extensionId)
            .switchIfEmpty(Mono.error(ExtensionNotFoundException(extensionId)))
    }

    fun updateExtensionRegex(extensionId: String, regex: String): Mono<com.gcgenome.rms.tables.pojos.Extension> {
        return Mono.from(checkExtensionById(extensionId)
            .flatMap { dslContext.updateExtension(extensionId, regex) })
    }

    fun insertServiceExtension(serviceExtension: ServiceExtension): Mono<ServiceExtension> {
        return Mono.from(dslContext.insertExtensionByService(serviceExtension))
    }

    fun deleteServiceExtension(serviceId: String, extensionId: String): Mono<ServiceExtension> {
        return Mono.from(dslContext.deleteExtensionByService(serviceId, extensionId))
    }

    fun buildExtensionIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("extension.id").likeIgnoreCase("%${filter.value}%")
            .or(field("extension.name").likeIgnoreCase("%${filter.value}%"))
    }
}