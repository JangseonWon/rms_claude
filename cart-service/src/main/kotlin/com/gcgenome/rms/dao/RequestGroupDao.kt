package com.gcgenome.rms.dao

import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.data.RequestGroupDTO
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.REQUEST_GROUP
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface RequestGroupDao {
    fun DSLContext.deleteRequestGroup(requestGroupId: UUID): Mono<RequestGroupDTO> {
        return Mono.from(
            deleteFrom(REQUEST_GROUP).where(REQUEST_GROUP.ID.eq(requestGroupId)).returning()
        ).map { it.into(RequestGroupDTO::class.java) }
    }
}