package com.gcgenome.rms.service

import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.ServiceExtensionDao
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.tables.pojos.SampleType
import com.gcgenome.rms.tables.pojos.ServiceSampleType
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ServiceSampleTypeHandler(
    val dslContext: DSLContext
): ServiceDao, ServiceExtensionDao, SampleTypeDao {

    fun selectSampleTypes(filter: Query.Companion.Filter, serviceId: String): Flux<SampleType> {
        return Flux.from(dslContext.run {
            selectServiceById(serviceId).switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
                .thenMany(selectServiceIdSampleTypeByNameOrId(serviceId, filter.value))
        })
    }

    fun selectSampleTypeAll(filter: Query.Companion.Filter): Flux<SampleType> {
        val whereClause = buildSampleTypeIdOrNameWhereClause(filter)
        return Flux.from(dslContext.selectSampleTypeByNameOrdId(whereClause))
    }

    fun insertServiceSampleType(serviceSampleType: ServiceSampleType): Mono<ServiceSampleType> {
        return Mono.from(dslContext.insertServiceSampleTypeByService(serviceSampleType))
    }

    fun deleteServiceSampleType(serviceSampleType: ServiceSampleType): Mono<ServiceSampleType> {
        return Mono.from(dslContext.deleteServiceSampleTypeById(serviceSampleType))
    }

    fun buildSampleTypeIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("sample_type.id").likeIgnoreCase("%${filter.value}%")
            .or(field("sample_type.name").likeIgnoreCase("%${filter.value}%"))
    }
}