package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestRelationDTO
import com.gcgenome.rms.tables.references.REQUEST_RELATION
import org.jooq.DSLContext
import reactor.core.publisher.Flux

interface RequestRelationDao {
    fun DSLContext.selectRequestRelations(): Flux<RequestRelationDTO> {
        return Flux.from(
            selectFrom(REQUEST_RELATION)
        ).map { it.into(RequestRelationDTO::class.java) }
    }
}