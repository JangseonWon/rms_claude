package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceExtension
import com.gcgenome.rms.tables.references.EXTENSION
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Flux

interface ExtensionDao{
    fun DSLContext.selectExtensionByService(serviceId: String): Flux<ServiceExtension> {
        return Flux.from(
            select(
                DSL.jsonObject(
                    DSL.key("id").value(EXTENSION.ID),
                    DSL.key("name").value(EXTENSION.NAME),
                    DSL.key("regex").value(EXTENSION.REGEX),
                    DSL.key("type").value(EXTENSION.TYPE),
                    DSL.key("required").value(SERVICE_EXTENSION.REQUIRED)
        )).from(EXTENSION)
                .join(SERVICE_EXTENSION).on(EXTENSION.ID.eq(SERVICE_EXTENSION.EXTENSION_ID))
                .join(SERVICE).on(SERVICE_EXTENSION.SERVICE_ID.eq(SERVICE.ID))
                .where(SERVICE.ID.eq(serviceId))
        ).map { it.into(ServiceExtension::class.java) }
    }
}