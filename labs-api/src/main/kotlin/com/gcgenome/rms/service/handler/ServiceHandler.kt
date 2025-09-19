package com.gcgenome.rms.service.handler

import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.service.dto.request.ServicePostDTO
import com.gcgenome.rms.service.dto.response.ServiceResponseDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
class ServiceHandler(
    private val dsl: DSLContext
): ServiceDao {
    fun searchServices(userId: UUID, servicePostDTO: ServicePostDTO): Flux<ServiceResponseDTO> {
        return dsl.searchServices(userId, servicePostDTO)
    }
}