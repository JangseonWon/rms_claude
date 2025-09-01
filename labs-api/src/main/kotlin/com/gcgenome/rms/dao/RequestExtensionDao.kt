package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestExtensionDTO
import com.gcgenome.rms.entity.RequestExtensionEntity
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface RequestExtensionDao {
    fun DSLContext.insertRequestExtension(requestExtensionEntity: RequestExtensionEntity): Mono<RequestExtensionDTO> {
        return Mono.from(
            insertInto(REQUEST_EXTENSION)
                .set(REQUEST_EXTENSION.ID, requestExtensionEntity.id)
                .set(REQUEST_EXTENSION.VALUE, requestExtensionEntity.value)
                .set(REQUEST_EXTENSION.EXTENSION_ID, requestExtensionEntity.extensionId)
                .set(REQUEST_EXTENSION.REQUEST_ID, requestExtensionEntity.requestId)
                .returning()
        ).map { it.into(RequestExtensionDTO::class.java) }
    }
    fun DSLContext.deleteRequestExtensionByRequestId(requestId: UUID): Mono<RequestExtensionDTO> {
        return Mono.from(
            deleteFrom(REQUEST_EXTENSION).where(REQUEST_EXTENSION.REQUEST_ID.eq(requestId))
                .returning()
        ).map { it.into(RequestExtensionDTO::class.java) }
    }
}