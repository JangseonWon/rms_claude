package com.gcgenome.rms.catalog.service

import com.gcgenome.rms.dao.ExtensionDao
import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.*
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import javax.management.ServiceNotFoundException

@Component
class ServiceHandler(val dslContext: DSLContext): ServiceDao, SampleTypeDao, UserDao, ExtensionDao {

    fun getServices(userId: String, categoryId: UUID): Flux<ServiceDTO> {
        return dslContext.selectServiceByUserIdAndCategoryId(userId, categoryId)
    }

    fun selectUserWithServices(userId: String, query: Query): Mono<UserDTO> {
        return dslContext.selectUserWithServicesQuery(userId, query)
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
}