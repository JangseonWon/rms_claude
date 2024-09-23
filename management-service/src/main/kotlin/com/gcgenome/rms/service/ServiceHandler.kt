package com.gcgenome.rms.service

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.ServiceNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ServiceHandler(
    val dslContext: DSLContext,
): ServiceDao, ServiceExtensionDao, ServiceSampleTypeDao {

    fun checkServiceById(serviceId: String): Mono<ServiceDTO> {
        return dslContext.selectServiceById(serviceId)
            .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
    }

    fun updateServiceById(service: ServiceDTO): Mono<ServiceDTO> {
        return checkServiceById(service.id!!).flatMap {
            dslContext.updateServiceById(service)
        }
    }

    fun selectServices(query: Query): Mono<Page<ServiceDTO>> {
        return dslContext.selectServicesWithPage(query)
    }

    fun selectService(serviceId: String): Mono<ServiceDTO> {
        return Mono.from(dslContext.selectServiceById(serviceId))
    }
    fun insertServiceSampleType(serviceSampleType: ServiceSampleTypeDTO): Mono<ServiceSampleTypeDTO> {
        return Mono.from(dslContext.insertServiceSampleTypeByService(serviceSampleType))
    }
    fun insertServiceExtension(serviceExtension: ServiceExtensionDTO): Mono<ServiceExtensionDTO> {
        return Mono.from(dslContext.insertServiceExtensionByService(serviceExtension))
    }
    fun deleteServiceExtension(serviceExtension: ServiceExtensionDTO): Mono<ServiceExtensionDTO> {
        return Mono.from(dslContext.deleteServiceExtensionById(serviceExtension))
    }
    fun deleteServiceSampleType(serviceSampleType: ServiceSampleTypeDTO): Mono<ServiceSampleTypeDTO> {
        return Mono.from(dslContext.deleteServiceSampleTypeById(serviceSampleType))
    }

}