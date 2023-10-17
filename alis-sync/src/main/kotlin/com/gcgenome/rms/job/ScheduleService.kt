package com.gcgenome.rms.job

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.data.RmsSampleExtension
import com.gcgenome.rms.data.Sample
import org.jooq.DSLContext
import org.jooq.Publisher
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Service
class ScheduleService (
    val dslContext: DSLContext
): AlisDao, OrderDao, ItemDao, SampleDao, PatientDao, OrganizationDao, ExtensionDao, UserDao{
    private val logger = LoggerFactory.getLogger("rms sync")

    @Scheduled(fixedDelay = 1000L*60*60)
    fun executeScheduledTask() {
        logger.info("schedule start")
        val sample = dataSyncBatch()
        logger.info("sampleId: ${sample.block()}" )
    }

    fun dataSyncBatch(): Mono<Void> {
        for(i: Int in 1..13){
            logger.info("===============================$i page")
            val sample = alisDatabaseSync(i, 20)
            logger.info("sampleId: ${sample.block()}" )
            Thread.sleep(5000)
        }
        return Mono.empty()
        /*return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectAlisOrderCount().flatMap { dataCount ->
                    val limitCount = 5
                    val batchCount = (dataCount / limitCount) + 1
                    Flux.range(0, batchCount)
                        .flatMap { page -> alisDatabaseSync(page, limitCount) }
                        .delayElements(Duration.ofSeconds(1)).then()
                }
            }
        })*/
    }

    fun alisDatabaseSync(page: Int, limitCount: Int): Mono<Item> {
        logger.info("fun start")
        return Mono.from(dslContext.transactionPublisher{ trx ->
            trx.dsl().run {
                logger.info("alis query start")
                    selectAlisOrderTest(page, limitCount).flatMap { alisOrder ->
                        val rmsOrder = RmsOrder.toModel(alisOrder)
                        logger.info("${rmsOrder.createAt}:${rmsOrder.genomeBarcode}:${rmsOrder.patientName} Data Check")
                        checkSample(rmsOrder.genomeBarcode)
                            .flatMap { sample -> checkItem(sample.itemId, rmsOrder.serviceId) }
                            .delayElement(Duration.ofMillis(100))
                            .switchIfEmpty(insertUser(rmsOrder)
                                .then(insertOrganization(rmsOrder))
                                .then(insertPatient(rmsOrder))
                                .then(insertOrder(rmsOrder))
                                .then(insertItem(rmsOrder))
                                .then(insertSample(rmsOrder))
                                .then(selectSampleExtension(rmsOrder.createAt, rmsOrder.orderNumber)
                                    .flatMap {  sampleExtension ->
                                        logger.info("========================= sampleExtension order_number: ${sampleExtension.orderNumber}")
                                        insertSampleExtension(sampleExtension, rmsOrder.sampleId)
                                    }
                                    .delayElements(Duration.ofMillis(100))
                                    .toMono()
                                ).then(checkItem(rmsOrder.sampleId, rmsOrder.serviceId))
                        )
                    }
            }
        })
    }
}