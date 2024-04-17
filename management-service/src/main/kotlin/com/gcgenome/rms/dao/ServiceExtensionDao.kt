package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.tables.references.EXTENSION
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import org.jooq.Condition
import org.jooq.DSLContext
import reactor.core.publisher.Flux


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
}