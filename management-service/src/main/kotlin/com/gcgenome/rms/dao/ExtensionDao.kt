package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisExtension
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.pojos.Extension
import com.gcgenome.rms.tables.references.EXTENSION
import com.gcgenome.rms.tables.references.SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ExtensionDao{
    fun DSLContext.selectExtensionsWithTotalPage(query: Query): Mono<Pair<Int, List<Extension>>> {
        val offset = (query.page - 1) * query.size

        val countMono = Mono.from(selectCount().from(EXTENSION))

        val extensionsFlux = Flux.from(
            selectFrom(EXTENSION)
                .orderBy(EXTENSION.ID.asc())
                .offset(offset)
                .limit(query.size)
        )
            .map { it.into(Extension::class.java) }
            .collectList()

        return Mono.zip(countMono, extensionsFlux)
            .map { tuple ->
                val totalPages = (tuple.t1.component1().toInt() + query.size - 1 )/ query.size
                totalPages to tuple.t2
            }
    }
    fun DSLContext.selectExtensionById(extensionId: String): Mono<Extension> {
        return Mono.from(
            selectFrom(EXTENSION).where(EXTENSION.ID.eq(extensionId))
        ).map { it.into(Extension::class.java) }
    }

    fun DSLContext.getExtensions(): Flux<Extension> {
        return Flux.from(
            selectFrom(EXTENSION).orderBy(EXTENSION.ID.asc())
        ).map { it.into(Extension::class.java) }
    }

    fun DSLContext.updateExtension(extensionId: String, regex: String): Mono<Extension> {
        return Mono.from(
            update(EXTENSION).set(EXTENSION.REGEX, regex)
                .where(EXTENSION.ID.eq(extensionId))
                .returning()
        ).map { it.into(Extension::class.java) }
    }
    fun DSLContext.upsertExtension(alisExtension: AlisExtension): Mono<Int> {
        return Mono.from(
            insertInto(EXTENSION)
                .set(EXTENSION.ID, alisExtension.customCode)
                .set(EXTENSION.NAME, alisExtension.customDisplayName)
                .onConflict(SERVICE.ID)
                .doUpdate()
                .set(SERVICE.NAME, alisExtension.customDisplayName)
        )
    }
}