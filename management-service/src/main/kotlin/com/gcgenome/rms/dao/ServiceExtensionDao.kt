package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.tables.pojos.ServiceExtension
import com.gcgenome.rms.tables.references.EXTENSION
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import org.jooq.Condition
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface ServiceExtensionDao{

    fun DSLContext.selectServiceExtensionByNameOrId(whereClause: Condition, serviceId: String): Flux<Extension> {
        return Flux.from(
            select(
                EXTENSION.ID,
                EXTENSION.NAME,
                EXTENSION.REGEX,
                SERVICE_EXTENSION.REQUIRED
            ).from(SERVICE_EXTENSION)
                .leftJoin(SERVICE).on(SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID))
                .leftJoin(EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                .where(SERVICE.ID.eq(serviceId).and(whereClause))
        ).map { it.into(Extension::class.java) }
    }

    fun DSLContext.selectExtensionByNameOrdId(where: Condition): Flux<Extension> {
        return Flux.from(
            selectFrom(EXTENSION).where(where)
        ).map { it.into(Extension::class.java) }
    }

    fun DSLContext.insertExtensionByService(serviceExtension: ServiceExtension): Mono<ServiceExtension> {
        return Mono.from(
            insertInto(SERVICE_EXTENSION)
                .set(SERVICE_EXTENSION.SERVICE_ID, serviceExtension.serviceId)
                .set(SERVICE_EXTENSION.EXTENSION_ID, serviceExtension.extensionId)
                .set(SERVICE_EXTENSION.REQUIRED, serviceExtension.required)
                .onDuplicateKeyIgnore()
                .returning()
        ).map { it.into(ServiceExtension::class.java) }
    }

    fun DSLContext.deleteExtensionByService(serviceId: String, extensionId: String): Mono<ServiceExtension> {
        return Mono.from(
            deleteFrom(SERVICE_EXTENSION)
                .where(SERVICE_EXTENSION.SERVICE_ID.eq(serviceId)
                    .and(SERVICE_EXTENSION.EXTENSION_ID.eq(extensionId)))
                .returning()
        ).map { it.into(ServiceExtension::class.java) }
    }
}