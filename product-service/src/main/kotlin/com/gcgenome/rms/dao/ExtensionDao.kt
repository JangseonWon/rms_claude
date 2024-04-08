package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Extension
import com.gcgenome.rms.data.ServiceExtension
import com.gcgenome.rms.tables.pojos.SampleExtension
import com.gcgenome.rms.tables.records.ServiceExtensionRecord
import com.gcgenome.rms.tables.references.EXTENSION
import com.gcgenome.rms.tables.references.SAMPLE_EXTENSION
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.SERVICE_EXTENSION
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
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


    fun DSLContext.insertSampleExtension(sampleExtension: Extension, sampleId: UUID): Mono<SampleExtension> {
        return Mono.from(
            insertInto(SAMPLE_EXTENSION)
                .set(SAMPLE_EXTENSION.EXTENSION_ID, sampleExtension.id)
                .set(SAMPLE_EXTENSION.SAMPLE_ID, sampleId)
                .set(SAMPLE_EXTENSION.VALUE, sampleExtension.value)
                .returning()
        ).map { it.into(SampleExtension::class.java) }
    }

    fun DSLContext.checkExtensionIdByService(serviceId: String, extensionId: String): Mono<ServiceExtensionRecord> {
        return Mono.from(
            selectFrom(SERVICE_EXTENSION)
                .where(SERVICE_EXTENSION.SERVICE_ID.eq(serviceId).and(SERVICE_EXTENSION.EXTENSION_ID.eq(extensionId)))
        )
    }

    fun DSLContext.deleteExtension(sampleId: UUID) =
        deleteFrom(SAMPLE_EXTENSION).where(SAMPLE_EXTENSION.SAMPLE_ID.eq(sampleId)).toMono()
}