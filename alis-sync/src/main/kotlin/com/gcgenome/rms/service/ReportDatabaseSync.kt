package com.gcgenome.rms.service

import com.gcgenome.rms.dao.ReportDao
import com.gcgenome.rms.dao.SampleDao
import com.gcgenome.rms.data.LibraFile
import com.gcgenome.rms.data.Report
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class ReportDatabaseSync(
    private val dslContext: DSLContext
): ReportDao, SampleDao {
    private val logger = LoggerFactory.getLogger("report sync")

    fun reportDatabaseSync(path: String, reportId: UUID, filePath: String): Mono<Report> {
        logger.info("reportDatabaseSync Start")
        val file = LibraFile.libraToModel(path, reportId, filePath, 0)
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                checkSampleByServiceId(file.genomeBarcode, file.serviceId)
                .switchIfEmpty(Mono.empty())
                .flatMap { sample1 -> updateSampleByGenomeBarcode(sample1.id!!) }
                .flatMap { sample2 -> insertReport(sample2.id!!, reportId, file) }
            }
        })
    }
}
