package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ReportDTO
import com.gcgenome.rms.tables.references.REPORT
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface ReportDao{
    fun DSLContext.deleteReportByServiceIdAndSampleId(serviceId: String, sampleId: UUID): Mono<ReportDTO> {
        return Mono.from(
            deleteFrom(REPORT)
                .where(
                    REPORT.SERVICE_ID.eq(serviceId),
                    REPORT.SAMPLE_ID.eq(sampleId)
                ).returning()
        ).map { it.into(ReportDTO::class.java) }
    }
}