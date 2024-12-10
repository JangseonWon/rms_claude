package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ReportDTO
import com.gcgenome.rms.tables.references.REPORT
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface ReportDao{
    fun DSLContext.deleteReportByOrderId(orderId: UUID): Mono<ReportDTO> {
        return Mono.from(
            deleteFrom(REPORT)
                .where(REPORT.ORDER_ID.eq(orderId)).returning()
        ).map { it.into(ReportDTO::class.java) }
    }
}