package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceExtension
import com.gcgenome.rms.tables.references.EXTENSION
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import java.util.*

interface ExtensionDao{
    fun DSLContext.selectExtensionByService(serviceId: String): Flux<ServiceExtension> {
        return Flux.from(
            select(
                DSL.jsonObject(
                    DSL.key("id").value(EXTENSION.ID),
                    DSL.key("name").value(EXTENSION.NAME),
                    DSL.key("regex").value(EXTENSION.REGEX),
                    DSL.key("required").value(SERVICE_EXTENSION.REQUIRED)
        )).from(EXTENSION)
                .join(SERVICE_EXTENSION).on(EXTENSION.ID.eq(SERVICE_EXTENSION.EXTENSION_ID))
                .join(SERVICE).on(SERVICE_EXTENSION.SERVICE_ID.eq(SERVICE.ID))
                .where(SERVICE.ID.eq(serviceId))
        ).map { it.into(ServiceExtension::class.java) }
    }

    fun DSLContext.selectExtensionByCategory(categoryId: UUID): Flux<ServiceExtension> {
        return Flux.from(
            select(
                DSL.jsonObject(
                    DSL.key("id").value(DSL.field(DSL.name("ext", "id"))),
                    DSL.key("name").value(DSL.field(DSL.name("ext", "name"))),
                    DSL.key("regex").value(DSL.field(DSL.name("ext", "regex"))),
                    DSL.key("required").value(DSL.field(DSL.name("ext", "required")))
                )).from(
                    select(EXTENSION.ID, EXTENSION.NAME, EXTENSION.REGEX, SERVICE_EXTENSION.REQUIRED)
                        .distinctOn(EXTENSION.ID)
                        .from(EXTENSION)
                        .join(SERVICE_EXTENSION).on(EXTENSION.ID.eq(SERVICE_EXTENSION.EXTENSION_ID))
                        .join(SERVICE).on(SERVICE_EXTENSION.SERVICE_ID.eq(SERVICE.ID))
                        .where(SERVICE.CATEGORY_ID.eq(categoryId))
                        .asTable("ext")
            )
        ).map { it.into(ServiceExtension::class.java) }
    }
}