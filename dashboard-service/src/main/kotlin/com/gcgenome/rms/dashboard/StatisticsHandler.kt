package com.gcgenome.rms.statistics

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.StatusCount
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class StatisticsHandler(
    val dslContext: DSLContext
): RequestDao {
    fun selectStatus(user : User) : Mono<StatusCount> {
        return dslContext.selectStatusCount(user)
    }
}