package com.gcgenome.rms.dao

import com.gcgenome.rms.service.dto.response.ExtensionResponseDTO
import com.gcgenome.rms.tables.references.EXTENSION
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface ExtensionDao {
    fun DSLContext.selectExtensionByCode(code:String): Mono<ExtensionResponseDTO> {
        return Mono.from(
            selectFrom(EXTENSION).where(EXTENSION.CODE.eq(code))
        ).map { it.into(ExtensionResponseDTO::class.java) }
    }
}