package com.gcgenome.rms.job

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.service.ReportDatabaseSync
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduleService (
    val alisDatabaseSync: AlisDatabaseSync,
    val libraDataS3Transfer: LibraDataS3Transfer,
    val fileServerDataS3Transfer: FileServerDataS3Transfer
): AlisDao {
    private val logger = LoggerFactory.getLogger("rms sync")

    @Scheduled(fixedDelay = 1000L*60*60*24)
    fun executeScheduledTask() {
        logger.info("schedule start")
//        alisDatabaseSync.dataSyncBatchSample()
//        .doOnSuccess { sample -> logger.info("BATCH SUCCESS: $sample") }
//        .subscribe()
        alisDatabaseSync.dataSyncBatchAlisFile().subscribe()
//        libraDataS3Transfer.libraDataTransfer()
    }
}