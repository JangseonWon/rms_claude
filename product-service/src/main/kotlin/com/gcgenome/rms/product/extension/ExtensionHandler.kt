package com.gcgenome.rms.product.extension

import com.gcgenome.rms.dao.ExtensionDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.ServiceExtension
import com.gcgenome.rms.exception.ServiceNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ExtensionHandler(val dslContext: DSLContext): ExtensionDao, ServiceDao {

    fun extensionByServiceId(serviceId: String): Flux<ServiceExtension> {
        return dslContext.run {
            checkServiceId(serviceId)
                .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
                .flatMapMany {selectExtensionByService(serviceId)}
        }
    }
}

