package com.gcgenome.lims.dao

import com.gcgenome.lims.data.Request
import com.gcgenome.lims.data.Status
import com.gcgenome.lims.data.WorkflowMessage
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.UUID

interface RequestDao {
    fun DSLContext.selectRequestBySampleIdAndServiceId(sampleId: UUID, serviceId: String): Mono<Request> {
        return Mono.from(
            selectFrom(REQUEST)
                .where(REQUEST.SERVICE_ID.eq(serviceId))
                .and(REQUEST.SAMPLE_ID.eq(sampleId))
        ).map { it.into(Request::class.java) }
    }

    fun DSLContext.updateRequestStatusById(serviceId: String, sampleId: UUID, message: WorkflowMessage, status: Status): Mono<Request> {
        val updateStatement = update(REQUEST)
            .set(REQUEST.STATUS, status.name)

        when (status) {
            Status.IN_PROGRESS -> { updateStatement.set(REQUEST.LIMS_RECEIVED_AT, LocalDateTime.now()) }
            Status.DELIVERED -> { updateStatement.set(REQUEST.LIMS_COMPLETED_AT, LocalDateTime.now()) }
            Status.TEST_FAILED -> {
                updateStatement.set(REQUEST.LIMS_RESAMPLE_AT, LocalDateTime.now())
                updateStatement.set(REQUEST.LIMS_RESAMPLE_REASON, message.param?.reason)
            }
            else -> {}
        }

        return Mono.from(
            updateStatement
                .where(REQUEST.SERVICE_ID.eq(serviceId),REQUEST.SAMPLE_ID.eq(sampleId))
                .returning()
        ).map { it.into(Request::class.java) }
    }
}