package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisSampleExtension
import com.gcgenome.rms.data.RmsSampleExtension
import com.gcgenome.rms.tables.references.ALIS_EXTENSION_VALUE
import com.gcgenome.rms.tables.references.SAMPLE_EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface ExtensionDao{
    fun DSLContext.insertSampleExtension(sampleExtension: AlisSampleExtension, sampleId: UUID): Mono<RmsSampleExtension> {
        return Mono.from(
            insertInto(SAMPLE_EXTENSION)
                .set(SAMPLE_EXTENSION.EXTENSION_ID, sampleExtension.extensionCode)
                .set(SAMPLE_EXTENSION.SAMPLE_ID, sampleId)
                .set(SAMPLE_EXTENSION.VALUE, sampleExtension.extensionValue)
                .onDuplicateKeyUpdate()
                .set(SAMPLE_EXTENSION.VALUE, sampleExtension.extensionValue)
                .returning()
        ).map { it.into(RmsSampleExtension::class.java) }
    }
    fun DSLContext.selectSampleExtension(orderDate: LocalDateTime, orderNumber: Int): Flux<AlisSampleExtension> {
        return Flux.from(
            selectFrom(ALIS_EXTENSION_VALUE).where(ALIS_EXTENSION_VALUE.ORDER_DATE.eq(orderDate).and(
                ALIS_EXTENSION_VALUE.ORDER_NUMBER.eq(orderNumber))))
            .map { it.into(AlisSampleExtension::class.java)}
    }


}