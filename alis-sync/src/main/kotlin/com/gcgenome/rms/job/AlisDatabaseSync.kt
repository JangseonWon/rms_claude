package com.gcgenome.rms.job

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.data.Sample
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@Service
class AlisDatabaseSync (
    private val dslContext: DSLContext
): AlisDao, OrderDao, ItemDao, SampleDao, PatientDao, OrganizationDao, ExtensionDao, UserDao {
    private val logger = LoggerFactory.getLogger("rms sync")

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
}