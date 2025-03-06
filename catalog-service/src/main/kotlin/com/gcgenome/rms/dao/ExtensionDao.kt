package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceExtensionDTO
import com.gcgenome.rms.tables.references.EXTENSION
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux

interface ExtensionDao {
    fun DSLContext.selectExtensionByService(serviceId: String): Flux<ServiceExtensionDTO> {
        return Flux.from(
            select(
                jsonObject(
                    key("id").value(EXTENSION.ID),
                    key("name").value(EXTENSION.NAME),
                    key("regex").value(EXTENSION.REGEX),
                    key("type").value(EXTENSION.TYPE),
                    key("required").value(SERVICE_EXTENSION.REQUIRED),
                    key("sort_extension").value(SERVICE_EXTENSION.SORT_EXTENSION)
                )).from(EXTENSION)
                .join(SERVICE_EXTENSION).on(EXTENSION.ID.eq(SERVICE_EXTENSION.EXTENSION_ID))
                .join(SERVICE).on(SERVICE_EXTENSION.SERVICE_ID.eq(SERVICE.ID))
                .where(SERVICE.ID.eq(serviceId))
        ).map { it.into(ServiceExtensionDTO::class.java) }
    }
}