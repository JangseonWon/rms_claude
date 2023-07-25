package com.gcgenome.rms.service

import com.gcgenome.rms.data.Service
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class Handler(
    val dslContext: DSLContext
):Dao {
    fun list(userId: String): Flux<Service> {
        return dslContext.selectUserService(userId)
    }
}