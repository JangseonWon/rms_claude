package com.gcgenome.rms.labs.service

import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.ServiceDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
class ServiceHandler(
    private val dsl: DSLContext
): ServiceDao {
    fun searchServices(userId: UUID, serviceDTO: ServiceDTO): Flux<ServiceDTO> {
        return dsl.searchServices(userId, serviceDTO)
    }
}