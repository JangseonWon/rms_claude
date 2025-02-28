package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceExtensionDTO
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Mono


interface ServiceExtensionDao{
    fun DSLContext.insertServiceExtension(serviceExtension: ServiceExtensionDTO): Mono<ServiceExtensionDTO> {
        return Mono.from(
            insertInto(SERVICE_EXTENSION)
                .set(SERVICE_EXTENSION.SERVICE_ID, serviceExtension.serviceId)
                .set(SERVICE_EXTENSION.EXTENSION_ID, serviceExtension.extensionId)
                .set(SERVICE_EXTENSION.REQUIRED, serviceExtension.required)
                .set(SERVICE_EXTENSION.SORT_EXTENSION, serviceExtension.sortExtension)
                .onDuplicateKeyIgnore()
                .returning()
        ).map { it.into(ServiceExtensionDTO::class.java) }
    }
    fun DSLContext.deleteServiceExtensionByServiceId(serviceId: String): Mono<ServiceExtensionDTO> {
        return Mono.from(
            deleteFrom(SERVICE_EXTENSION)
                .where(SERVICE_EXTENSION.SERVICE_ID.eq(serviceId))
                .returning()
        ).map { it.into(ServiceExtensionDTO::class.java) }
    }
}