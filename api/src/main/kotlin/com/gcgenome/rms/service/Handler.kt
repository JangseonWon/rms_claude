package com.gcgenome.rms.service

import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.Service
import org.jooq.DSLContext
import reactor.core.publisher.Flux

@org.springframework.stereotype.Service("com.gcgenome.rms.service.Handler")
class Handler(
    val dslContext: DSLContext
):ServiceDao {
    fun list(userId: String): Flux<Service> {
        return dslContext.selectUserService(userId)
    }
}