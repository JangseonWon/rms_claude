package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceExtensionDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import java.util.UUID

interface ServiceExtensionDao {
    fun DSLContext.selectServiceExtensionByServiceId(serviceId: UUID): Flux<ServiceExtensionDTO> {
        return Flux.from(
            select(
                SERVICE_EXTENSION.ID,
                SERVICE_EXTENSION.IS_REQUIRED,
                SERVICE_EXTENSION.SERVICE_ID,
                SERVICE_EXTENSION.EXTENSION_ID,
                EXTENSION
            ).from(SERVICE_EXTENSION)
                .join(EXTENSION).on(EXTENSION.ID.eq(SERVICE_EXTENSION.EXTENSION_ID))
                .where(SERVICE_EXTENSION.SERVICE_ID.eq(serviceId))
        ).map { it.into(ServiceExtensionDTO::class.java) }
    }
}