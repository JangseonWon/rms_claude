package com.gcgenome.rms.service

import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.data.Service_.Companion.groupBySampleType
import com.gcgenome.rms.data.Service_.Companion.groupByService
import com.gcgenome.rms.entity.QExtension.extension
import com.gcgenome.rms.entity.QOrganizationService.organizationService
import com.gcgenome.rms.entity.QSampleType.sampleType
import com.gcgenome.rms.entity.QService.service
import com.gcgenome.rms.entity.QServiceExtension.serviceExtension
import com.gcgenome.rms.entity.QServiceSampleType.serviceSampleType
import com.gcgenome.rms.repo.ServiceRepository
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Projections
import com.querydsl.sql.SQLQuery
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository("com.gcgenome.rms.service.Dao")
class Dao(
    val repo: ServiceRepository,
) {
    private fun <T> SQLQuery<T>.fromServiceAndOrganization(): SQLQuery<T> = from(service)
        .rightJoin(organizationService).on(service.id.eq(organizationService.serviceId))

    private fun <T> SQLQuery<T>.fromServiceAndExtension(): SQLQuery<T> = from(service)
        .rightJoin(serviceExtension).on(service.id.eq(serviceExtension.serviceId))
        .leftJoin(extension).on(serviceExtension.extensionId.eq(extension.id))

    private fun toSampleExtensions(): Expression<Service_.Companion.ServiceExtensionBuilder> = Projections.constructor (
        Service_.Companion.ServiceExtensionBuilder::class.java,
        service.id.`as`("service"),
        extension.id.`as`("id"),
        extension.name.`as`("name"),
        serviceExtension.required.`as`("required"),
        extension.regex.`as`("regex")
    )

    private fun <T> SQLQuery<T>.fromServiceAndSampleType(): SQLQuery<T> = from(service)
        .rightJoin(serviceSampleType).on(service.id.eq(serviceSampleType.serviceId))
        .rightJoin(sampleType).on(serviceSampleType.sampleTypeId.eq(sampleType.id))

    private fun toSampleTypes(): Expression< Service_.Companion.ServiceSampleTypeBuilder> = Projections.constructor (
        Service_.Companion.ServiceSampleTypeBuilder::class.java,
        service.id.`as`("service"),
        sampleType.id.`as`("id"),
        sampleType.name.`as`("name")
    )

    private fun toServices(): Expression<Service_.Companion.ServiceBuilder> = Projections.constructor (
        Service_.Companion.ServiceBuilder::class.java,
        service.id.`as`("id"),
        service.name.`as`("name")
    )

    fun findByUserService(userId: String): Flux<Service_> = repo.query {
        it.select(toServices()).fromServiceAndOrganization().where(organizationService.userId.eq(userId))
    }.all().collectList().flatMap {
        val services = it.stream().map (Service_.Companion.ServiceBuilder::id).toList()
        val extensions = repo.query { it.select(toSampleExtensions()).fromServiceAndExtension().where(service.id.`in`(services)) }.all().groupByService().collectList()
        val sampleTypes = repo.query { it.select(toSampleTypes()).fromServiceAndSampleType().where(service.id.`in`(services)) }.all().groupBySampleType().collectList()
        extensions.zipWith(sampleTypes).map { tuples ->
            val extensions = tuples.t1.associateBy { it.first }
            val sampleTypes = tuples.t2.associateBy { it.first }
            it.stream().map { sb ->
                sb.build(
                    extensions[sb.id]?.second?: emptyList(),
                    sampleTypes[sb.id]?.second?: emptyList()
                )
            }
        }
    }.flatMapMany { Flux.fromStream(it) }
}
