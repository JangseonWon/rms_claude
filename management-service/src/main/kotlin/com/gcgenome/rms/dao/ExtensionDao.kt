package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisExtension
import com.gcgenome.rms.data.ExtensionDTO
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.references.EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface ExtensionDao : QueryDao{

    fun DSLContext.selectExtensionsWithPage(query: Query): Mono<Page<ExtensionDTO>> {
        return selectPage(EXTENSION, query) {record ->
            record.into(ExtensionDTO::class.java)
        }
    }
    fun DSLContext.selectExtensionById(extensionId: String): Mono<ExtensionDTO> {
        return Mono.from(
            selectFrom(EXTENSION).where(EXTENSION.ID.eq(extensionId))
        ).map { it.into(ExtensionDTO::class.java) }
    }

    fun DSLContext.updateExtension(extension: ExtensionDTO): Mono<ExtensionDTO> {
        return Mono.from(
            update(EXTENSION)
                .set(EXTENSION.REGEX, extension.regex)
                .set(EXTENSION.TYPE, extension.type)
                .where(EXTENSION.ID.eq(extension.id))
                .returning()
        ).map { it.into(ExtensionDTO::class.java) }
    }
    fun DSLContext.upsertExtension(alisExtension: AlisExtension): Mono<Int> {
        return Mono.from(
            insertInto(EXTENSION)
                .set(EXTENSION.ID, alisExtension.customCode)
                .set(EXTENSION.NAME, alisExtension.customDisplayName)
                .onConflict(EXTENSION.ID)
                .doUpdate()
                .set(EXTENSION.NAME, alisExtension.customDisplayName)
        )
    }
}