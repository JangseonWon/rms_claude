package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestGroupDTO
import com.gcgenome.rms.tables.references.REQUEST_GROUP
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.UUID

interface RequestGroupDao {
    fun DSLContext.insertRequestGroup(): Mono<RequestGroupDTO> {
        return Mono.from(
            insertInto(REQUEST_GROUP)
                .set(REQUEST_GROUP.ID, UUID.randomUUID())
                .returning()
        ).map { it.into(RequestGroupDTO::class.java) }
    }
}