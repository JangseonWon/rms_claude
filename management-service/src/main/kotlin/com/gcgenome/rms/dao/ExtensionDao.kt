package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.Extension
import com.gcgenome.rms.tables.references.EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ExtensionDao{
    fun DSLContext.getExtensions(): Flux<Extension> {
        return Flux.from(
            selectFrom(EXTENSION)
        ).map { it.into(Extension::class.java) }
    }

    fun DSLContext.updateExtension(extensionId: String, regex: String): Mono<Extension> {
        return Mono.from(
            update(EXTENSION).set(EXTENSION.REGEX, regex)
                .where(EXTENSION.ID.eq(extensionId))
                .returning()
        ).map { it.into(Extension::class.java) }
    }
}