package com.gcgenome.rms.service

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ServiceHandler(
    val dslContext: DSLContext,
): ServiceDao, ServiceExtensionDao, ServiceSampleTypeDao {
    fun updateServiceById(service: ServiceDTO): Mono<Void> {
        return Mono.from(
            dslContext.transactionPublisher { trx ->
                trx.dsl().run {
                    updateServiceById(service)
                        .then(deleteServiceSampleTypeByServiceId(service.id!!))
                        .thenMany(Flux.fromIterable(service.sampleTypes ?: emptyList())
                            .flatMap { sampleType ->
                                insertServiceSampleTypeByService(ServiceSampleTypeDTO(serviceId = service.id, sampleTypeId = sampleType.id))
                            })
                        .then(deleteServiceExtensionByServiceId(service.id!!))
                        .thenMany(Flux.fromIterable(service.extensions ?: emptyList())
                            .flatMap { extension ->
                                insertServiceExtension(ServiceExtensionDTO(serviceId = service.id, extensionId = extension.id, required = extension.required))
                            })
                        .then()
                }
            }
        )
    }

    fun selectServices(query: Query): Mono<Page<ServiceDTO>> {
        return dslContext.selectServicesWithPage(query)
    }
    fun selectServices(): Flux<ServiceDTO> {
        return dslContext.selectServices()
    }

    fun selectService(serviceId: String): Mono<ServiceDTO> {
        return Mono.from(dslContext.selectServiceById(serviceId))
    }

}