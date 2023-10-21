package com.gcgenome.rms.job

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.data.Sample
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class ScheduleService (
    val dslContext: DSLContext
): AlisDao, OrderDao, ItemDao, SampleDao, PatientDao, OrganizationDao, ExtensionDao, UserDao{
    private val logger = LoggerFactory.getLogger("rms sync")

    @Scheduled(fixedDelay = 1000L*60*60)
    fun executeScheduledTask() {
        logger.info("schedule start")
        dataSyncBatch()
        .doOnSuccess { sample -> logger.info("BATCH SUCCESS: ${sample}") }
        .subscribe()
    }

    fun dataSyncBatch(): Mono<Void> {
        val fromDate = "2023-01-02T00:00:00"
        val toDate = "2023-01-02T23:00:00"
        val limit = 20

        return dslContext.selectAlisOrderCount(fromDate, toDate)
            .flatMap { count ->
                logger.info("count : $count")
                val batchCount = count / limit + if (count % limit == 0) 0 else 1
                Flux.range(1, batchCount)
                    .flatMap { i -> alisDatabaseSync(i, limit, fromDate, toDate)
                        .doOnSuccess { logger.info("TOTAL COUNT : $count / BATCH COUNT : $batchCount / CURRENT COUNT : $i") } }
                    .then()
            }
    }

    fun alisDatabaseSync(page: Int, limit: Int, fromDate: String, toDate: String): Mono<Sample> {
        logger.info("fun start")
        return Mono.from(dslContext.transactionPublisher{ trx ->
            trx.dsl().run {
                logger.info("alis query start")
                selectAlisOrder(page - 1, limit, fromDate, toDate).flatMap { alisOrder ->
                    val rmsOrder = RmsOrder.toModel(alisOrder)
                    checkSampleByServiceId(rmsOrder.genomeBarcode, rmsOrder.serviceId)
                        .flatMap {
                            checkSampleByServiceId(rmsOrder.genomeBarcode, rmsOrder.serviceId)
                            }.switchIfEmpty(insertUser(rmsOrder)
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

}