package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.RequestGroupDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface RequestGroupDao {
    fun DSLContext.insertRequestGroup(): Mono<RequestGroupDTO> {
        return Mono.from(
            insertInto(REQUEST_GROUP)
                .set(REQUEST_GROUP.ID, UUID.randomUUID())
                .returning()
        ).map { it.into(RequestGroupDTO::class.java) }
    }

    fun DSLContext.deleteRequestGroupById(id: UUID): Mono<RequestDTO> {
        return Mono.from(
            deleteFrom(REQUEST_GROUP)
                .where(REQUEST_GROUP.ID.eq(id)
                    .andNotExists(selectOne().from(REQUEST).where(REQUEST.REQUEST_GROUP_ID.eq(id)))
                ).returning()
        ).map { it.into(RequestDTO::class.java) }
    }
}