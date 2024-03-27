package com.gcgenome.rms.service

import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.tables.pojos.Service
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
class ServiceHandler (
    val dslContext: DSLContext
): ServiceDao {
    fun selectService(categoryId: UUID, userId: String): Flux<Service> {
        return dslContext.selectService(categoryId, userId)
    }
}