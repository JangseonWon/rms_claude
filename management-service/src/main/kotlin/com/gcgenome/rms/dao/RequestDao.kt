package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.util.*

interface RequestDao {
    fun DSLContext.deleteRequestById(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            deleteFrom(REQUEST).where(
                    REQUEST.SAMPLE_ID.eq(request.sample!!.id),
                    REQUEST.SERVICE_ID.eq(request.service!!.id)
            ).returning()
        ).map { it.into(RequestDTO::class.java) }
    }
}