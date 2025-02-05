package com.gcgenome.lims.dao

import com.gcgenome.lims.data.Report
import com.gcgenome.rms.tables.references.REPORT
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.UUID

interface ReportDao {
    fun DSLContext.insertReport(report: Report): Mono<Report> {
        return Mono.from(
            insertInto(REPORT)
                .set(REPORT.ID, report.id)
                .set(REPORT.TYPE, report.type)
                .set(REPORT.VALUE, report.value)
                .set(REPORT.CREATE_AT, report.createAt)
                .set(REPORT.IS_LATEST, report.isLatest)
                .set(REPORT.SERVICE_ID, report.serviceId)
                .set(REPORT.SAMPLE_ID, report.sampleId)
                .returning()
        ).map { it.into(Report::class.java) }
    }
    fun DSLContext.updateReportIsLatestBySampleIdAndServiceId(sampleId: UUID, serviceId: String): Mono<Report>{
        return Mono.from(
            update(REPORT)
                .set(REPORT.IS_LATEST, false)
                .where(
                    REPORT.SAMPLE_ID.eq(sampleId),
                    REPORT.SERVICE_ID.eq(serviceId),
                    REPORT.TYPE.eq("PDF")
                )
                .returning()
        ).map { it.into(Report::class.java) }
    }
}