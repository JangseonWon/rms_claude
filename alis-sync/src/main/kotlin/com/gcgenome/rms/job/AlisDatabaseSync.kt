package com.gcgenome.rms.job

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*


@Service
class AlisDatabaseSync (
    private val dslContext: DSLContext,
    val fileServerDataS3Transfer: FileServerDataS3Transfer
): AlisDao, OrderDao, ItemDao, SampleDao, PatientDao, OrganizationDao, ExtensionDao, UserDao, ReportDao {
    private val logger = LoggerFactory.getLogger("rms sync")

    fun dataSyncBatchSample(fromDate: String, toDate: String, limit: Int): Mono<Void> {
        return dslContext.selectAlisOrderCount(fromDate, toDate)
            .flatMap { count ->
                logger.info("ALIS SAMPLE count : $count")
                val batchCount = count / limit + if (count % limit == 0) 0 else 1
                Flux.range(1, batchCount)
                    .flatMap { i -> alisDatabaseSync(i, limit, fromDate, toDate)
                        .doOnSuccess { logger.info("ALIS SAMPLE : TOTAL COUNT : $count / BATCH COUNT : $batchCount / CURRENT COUNT : $i") } }
                    .then()
            }
    }
    fun dataSyncBatchAlisFile(fromDate: String, toDate: String, limit: Int): Mono<Void> {
        return dslContext.selectAlisOrderFileCount(fromDate, toDate)
            .flatMap { count ->
                logger.info("ALIS FILE count : $count")
                val batchCount = count / limit + if (count % limit == 0) 0 else 1
                Flux.range(1, batchCount)
                    .flatMap { i -> fileServerDatabaseSync(i, limit, fromDate, toDate)
                        .doOnSuccess { logger.info("ALIS FILE : TOTAL COUNT : $count / BATCH COUNT : $batchCount / CURRENT COUNT : $i") } }
                    .then()
            }
    }
    fun alisDatabaseSync(page: Int, limit: Int, fromDate: String, toDate: String): Mono<Sample> {
        logger.info("rms sync fun start")
        return Mono.from(dslContext.transactionPublisher{ trx ->
            trx.dsl().run {
                selectAlisOrder(page - 1, limit, fromDate, toDate).flatMap { alisOrder ->
                    val rmsOrder = RmsOrder.toModel(alisOrder)
                    checkSampleByServiceId(rmsOrder.genomeBarcode, rmsOrder.serviceId)
                        .switchIfEmpty(insertUser(rmsOrder)
                            .then(insertOrganization(rmsOrder))
                            .then(insertPatient(rmsOrder))
                            .then(insertOrder(rmsOrder))
                            .then(insertItem(rmsOrder))
                            .then(insertSample(rmsOrder))
                            .then(selectSampleExtension(rmsOrder.createAt, rmsOrder.orderNumber)
                                .flatMap {  sampleExtension ->
                                    insertSampleExtension(sampleExtension, rmsOrder.sampleId)
                                }.toMono()
                            ).then(checkSampleByServiceId(rmsOrder.genomeBarcode, rmsOrder.serviceId))
                        )
                }
            }
        })
    }

    fun fileServerDatabaseSync(page: Int, limit: Int, fromDate: String, toDate: String): Mono<Report> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectAlisOrderFile(page - 1, limit, fromDate, toDate).flatMap { alisFile ->
                    val s3File = FileServerFile.convertFileServerFile(alisFile)
                    val report = LibraFile.alisFileToModel(alisFile, s3File.s3Path, s3File.upperType, s3File.sequence)
                    checkSampleByServiceId(s3File.genomeBarcode, s3File.serviceCode).flatMap { sample ->
                        fileServerDataS3Transfer.fileServerDataS3Transfer(s3File.fileName, s3File.type, s3File.windowPath, s3File.s3Path, s3File.text)
                        insertReport(sample.id!!, UUID.randomUUID(), report)
                    }
                }
            }
        })
    }
}