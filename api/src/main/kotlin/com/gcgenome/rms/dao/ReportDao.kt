package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Report
import com.gcgenome.rms.tables.references.REPORT
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface ReportDao {
    fun DSLContext.selectReportById(reportId: UUID): Mono<Report>{
        return Mono.from(selectFrom(REPORT).where(REPORT.ID.eq(reportId)))
            .map { it.into(Report::class.java) }
    }
    fun DSLContext.saveReport(sampleId: UUID, reportDto: Report): Mono<Report> {
        val reportId = UUID.randomUUID()
        val genomeBarcode = reportDto.genomeBarcode!!
        val year = genomeBarcode.substring(0, 4)
        val month = genomeBarcode.substring(4, 6)
        val day = genomeBarcode.substring(6, 8)
        val userCode = genomeBarcode.substring(8, 11)
        val num = genomeBarcode.substring(11)
        val value = reportDto.value ?: "/api/reports/$reportId"
        return Mono.from(
            insertInto(REPORT)
                .set(REPORT.ID, reportId)
                .set(REPORT.CREATE_AT, LocalDateTime.now())
                .set(REPORT.TYPE, reportDto.type.name)
                .set(REPORT.VALUE, value)
                .set(REPORT.SAMPLE_ID, sampleId)
                .set(REPORT.PATH, "reports/$year/$month/$day/$userCode/$num/$reportId.${reportDto.type.name.lowercase()}")
                .returning()
        ).map { it.into(Report::class.java) }
    }

    fun DSLContext.updateReportReportedAt(reportId: UUID): Mono<Report> {
        return Mono.from(
            update(REPORT)
                .set(REPORT.REPORTED_AT, LocalDateTime.now())
                .where(REPORT.ID.eq(reportId))
                .returning()
        ).map { it.into(Report::class.java) }
    }
}