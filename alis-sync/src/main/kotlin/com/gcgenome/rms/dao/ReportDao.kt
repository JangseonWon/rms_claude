package com.gcgenome.rms.dao

import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.references.REPORT
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface ReportDao {
    fun DSLContext.insertReport(sampleId: UUID, reportId: UUID, report: LibraFile): Mono<Report> {
        return Mono.from(
            insertInto(REPORT)
                .set(REPORT.ID, reportId)
                .set(REPORT.CREATE_AT, report.createAt)
                .set(REPORT.PATH, report.path)
                .set(REPORT.TYPE, report.type)
                .set(REPORT.VALUE, report.value)
                .set(REPORT.SAMPLE_ID, sampleId)
                .set(REPORT.REPORTED_AT, report.reportedAt)
                .onDuplicateKeyIgnore()
                .returning()
        ).map { it.into(Report::class.java) }
    }
}