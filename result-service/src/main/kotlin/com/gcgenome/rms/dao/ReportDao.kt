package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ReportDTO
import com.gcgenome.rms.tables.references.ORDER
import com.gcgenome.rms.tables.references.REPORT
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.DSLContext
import org.jooq.impl.DSL.jsonObject
import org.jooq.impl.DSL.key
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface ReportDao{
    fun DSLContext.selectReportById(reportId: UUID): Mono<ReportDTO> {
        return Mono.from(
            select(
                REPORT.ID,
                REPORT.TYPE,
                REPORT.VALUE,
                REPORT.CREATE_AT,
                REPORT.DOWNLOADED_AT,
                REPORT.IS_LATEST,
                jsonObject(
                    key("order").value(jsonObject(
                        key("id").value(REQUEST.ORDER_ID),
                        key("user").value(jsonObject(
                            key("id").value(ORDER.USER_ID)
                        ))
                    )),
                    key("service").value(jsonObject(
                        key("id").value(REQUEST.SERVICE_ID)
                    )),
                    key("sample").value(jsonObject(
                        key("id").value(REQUEST.SAMPLE_ID)
                    ))
                ).`as`("request")
            ).from(REPORT)
                .leftJoin(REQUEST).on(
                    REPORT.ORDER_ID.eq(REQUEST.ORDER_ID),
                    REPORT.SERVICE_ID.eq(REQUEST.SERVICE_ID),
                    REPORT.SAMPLE_ID.eq(REQUEST.SAMPLE_ID)
                )
                .leftJoin(ORDER).on(REQUEST.ORDER_ID.eq(ORDER.ID))
                .where(REPORT.ID.eq(reportId))
        ).map { it.into(ReportDTO::class.java) }
    }
    fun DSLContext.updateReportReportedAt(reportId: UUID): Mono<ReportDTO>{
        return Mono.from(update(REPORT)
            .set(REPORT.DOWNLOADED_AT, LocalDateTime.now())
            .where(REPORT.ID.eq(reportId))
            .returning()
        ).map { it.into(ReportDTO::class.java) }
    }
}