package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ExtensionDTO
import com.gcgenome.rms.data.ServiceDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface ExtensionDao {
    fun DSLContext.selectExtensionBycode(code:String): Mono<ExtensionDTO> {
        return Mono.from(
            selectFrom(EXTENSION).where(EXTENSION.CODE.eq(code))
        ).map { it.into(ExtensionDTO::class.java) }
    }
}