package com.gcgenome.rms.request

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.PatientDao
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.*
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RequestHandler(
    val dslContext: DSLContext
): RequestDao, ServiceDao, PatientDao {
    fun selectRequests(user: UserAuthentication, query: Query):  Mono<Page<RequestDTO>> {
        return dslContext.selectRequestsWithPage(query, user.user)
    }
    fun selectService(serviceId: String): Flux<ServiceDTO> {
        return Flux.from(
            dslContext.selectServiceById(serviceId)
                .flatMapMany { service ->
                    if (service.type == "GENERAL") {
                        Flux.empty()
                    } else {
                        dslContext.selectServiceForGroupServiceId(service.groupName!!)
                    }
                }.flatMapSequential { relatedService ->
                    dslContext.selectServiceInfoById(relatedService.id!!)
                }
        )
    }
    fun selectPatients(user: UserAuthentication, query: Query):  Mono<Page<PatientDTO>> {
        return dslContext.selectPatientsWithPage(query, user.user)
    }
}