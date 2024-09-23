package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceExtensionDTO
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Mono


interface ServiceExtensionDao{
    fun DSLContext.insertServiceExtensionByService(serviceExtension: ServiceExtensionDTO): Mono<ServiceExtensionDTO> {
        return Mono.from(
            insertInto(SERVICE_EXTENSION)
                .set(SERVICE_EXTENSION.SERVICE_ID, serviceExtension.serviceId)
                .set(SERVICE_EXTENSION.EXTENSION_ID, serviceExtension.extensionId)
                .set(SERVICE_EXTENSION.REQUIRED, serviceExtension.required)
                .onDuplicateKeyIgnore()
                .returning()
        ).map { it.into(ServiceExtensionDTO::class.java) }
    }

    fun DSLContext.deleteServiceExtensionById(serviceExtension: ServiceExtensionDTO): Mono<ServiceExtensionDTO> {
        return Mono.from(
            deleteFrom(SERVICE_EXTENSION)
                .where(SERVICE_EXTENSION.SERVICE_ID.eq(serviceExtension.serviceId)
                    .and(SERVICE_EXTENSION.EXTENSION_ID.eq(serviceExtension.extensionId)))
                .returning()
        ).map { it.into(ServiceExtensionDTO::class.java) }
    }
}