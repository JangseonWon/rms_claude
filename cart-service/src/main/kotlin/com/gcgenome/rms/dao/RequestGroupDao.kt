package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestGroupDTO
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.REQUEST_GROUP
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface RequestGroupDao {
    fun DSLContext.deleteRequestGroup(requestGroupId: UUID): Mono<RequestGroupDTO> {
        return Mono.from(
            deleteFrom(REQUEST_GROUP)
                .where(REQUEST_GROUP.ID.eq(requestGroupId))
                .andNotExists(
                    selectOne()
                        .from(REQUEST)
                        .where(REQUEST.REQUEST_GROUP_ID.eq(requestGroupId))
                )
                .returning()
        ).map { it.into(RequestGroupDTO::class.java) }
    }
}