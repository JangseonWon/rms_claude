package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RequestExtensionDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface RequestExtensionDao {
    fun DSLContext.insertRequestExtension(requestExtension: RequestExtensionDTO): Mono<RequestExtensionDTO> {
        return Mono.from(
            insertInto(REQUEST_EXTENSION)
                .set(REQUEST_EXTENSION.ID, requestExtension.id)
                .set(REQUEST_EXTENSION.VALUE, requestExtension.value)
                .set(REQUEST_EXTENSION.EXTENSION_ID, requestExtension.extensionId)
                .set(REQUEST_EXTENSION.REQUEST_ID, requestExtension.requestId)
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