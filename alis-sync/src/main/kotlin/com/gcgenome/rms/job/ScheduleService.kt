package com.gcgenome.rms.job

import com.gcgenome.rms.dao.*
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ScheduleService (
    val dslContext: DSLContext,
    val alisDatabaseSync: AlisDatabaseSync
): AlisDao {
    private val logger = LoggerFactory.getLogger("rms sync")


    @Scheduled(fixedDelay = 1000L*60*60)
    fun executeScheduledTask() {
        logger.info("schedule start")
        dataSyncBatch()
        .doOnSuccess { sample -> logger.info("BATCH SUCCESS: ${sample}") }
        .subscribe()
    }

    fun dataSyncBatch(): Mono<Void> {
        val fromDate = "2023-01-01T00:00:00"
        val toDate = "2023-01-31T23:00:00"
        val limit = 20

        return dslContext.selectAlisOrderCount(fromDate, toDate)
            .flatMap { count ->
                logger.info("count : $count")
                val batchCount = count / limit + if (count % limit == 0) 0 else 1
                Flux.range(1, batchCount)
                    .flatMap { i -> alisDatabaseSync.alisDatabaseSync(i, limit, fromDate, toDate)
                        .doOnSuccess { logger.info("TOTAL COUNT : $count / BATCH COUNT : $batchCount / CURRENT COUNT : $i") } }
                    .then()
            }
    }

}