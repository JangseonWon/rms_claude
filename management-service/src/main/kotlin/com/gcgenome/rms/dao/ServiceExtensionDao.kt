package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.tables.pojos.Service
import com.gcgenome.rms.tables.pojos.ServiceExtension
import com.gcgenome.rms.tables.references.EXTENSION
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.impl.DSL.*
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
//                jsonArrayAgg(
//                    jsonObject(
//                        key("id").value(EXTENSION.ID),
//                        key("name").value(EXTENSION.NAME),
//                        key("regex").value(EXTENSION.REGEX),
//                        key("required").value(SERVICE_EXTENSION.REQUIRED),
//                    )
//                ).`as`("extensions")
            ).from(SERVICE_EXTENSION)
                .leftJoin(SERVICE).on(SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID))
                .leftJoin(EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                .where(SERVICE.ID.eq(serviceId).and(whereClause))
        ).map { it.into(Extension::class.java) }
    }
}