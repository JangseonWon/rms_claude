package com.gcgenome.lims.dao

import com.gcgenome.lims.data.Report
import com.gcgenome.rms.tables.references.REPORT
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface ReportDao {
    fun DSLContext.insertReport(report: Report): Mono<Report> {
        return Mono.from(
            insertInto(REPORT)
                .set(REPORT.ID, report.id)
                .set(REPORT.TYPE, report.type)
                .set(REPORT.VALUE, report.value)
                .set(REPORT.CREATE_AT, report.createAt)
                .set(REPORT.IS_LATEST, report.isLatest)
                .set(REPORT.ORDER_ID, report.orderId)
                .set(REPORT.SERVICE_ID, report.serviceId)
                .set(REPORT.SAMPLE_ID, report.sampleId)
                .returning()
        ).map { it.into(Report::class.java) }
    }
}