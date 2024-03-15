package com.gcgenome.rms.service

import com.gcgenome.rms.dao.ExtensionDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.ServiceExtension
import com.gcgenome.rms.exceptions.CategoryNotFoundException
import com.gcgenome.rms.exceptions.ServiceNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class ExtensionHandler(val dslContext: DSLContext): ExtensionDao, ServiceDao {

    fun extensionByServiceId(serviceId: String): Flux<ServiceExtension> {
        return dslContext.run {
            checkServiceId(serviceId)
                .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
                .flatMapMany {selectExtensionByService(serviceId)}
        }
    }

    /*fun extensionByCategoryId(categoryId: UUID): Flux<ServiceExtension> {
        return dslContext.run {
           checkCategoryId(categoryId)
               .switchIfEmpty(Mono.error(CategoryNotFoundException(categoryId.toString())))
               .then(selectServiceByCategoryId(categoryId))
               .flatMap { serviceIds ->
                   val serviceIdList = serviceIds.split(",").map { it.trim() }
                   selectExtensionByCategory(*serviceIdList.toTypedArray())
               }
        }
    }*/
}

