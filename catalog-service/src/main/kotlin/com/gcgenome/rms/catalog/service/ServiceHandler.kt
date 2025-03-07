package com.gcgenome.rms.catalog.service

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.ExtensionDao
import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import javax.management.ServiceNotFoundException

@Component
class ServiceHandler(val dslContext: DSLContext): ServiceDao, SampleTypeDao, ExtensionDao {
    fun getServices(user: UserAuthentication): Flux<ServiceDTO> {
        return dslContext.selectServices(user.user)
    }
    fun getServicesWithCategoryId(user: UserAuthentication, categoryId: UUID): Flux<ServiceDTO> {
        val andWhere = if (user.user.role == "USER") USER_SERVICE.USER_ID.eq(user.user.id) else DSL.noCondition()
        return dslContext.selectServiceByUserIdAndCategoryId(andWhere, categoryId)
    }

    fun getSampleTypes(serviceId: String): Flux<SampleTypeDTO> {
        return dslContext.selectSampleTypeByServiceId(serviceId)
    }

    fun extensionByServiceId(serviceId: String): Flux<ServiceExtensionDTO> {
        return dslContext.run {
            checkServiceId(serviceId)
                .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
                .flatMapMany {selectExtensionByService(serviceId)}
        }
    }

    fun selectServices(serviceId: String): Flux<ServiceDTO> {
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

    fun selectServiceById(serviceId: String): Mono<ServiceDTO> {
        return Mono.from(dslContext.selectServiceInfoById(serviceId))
    }
}