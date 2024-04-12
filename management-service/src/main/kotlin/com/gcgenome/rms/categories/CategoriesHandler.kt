package com.gcgenome.rms.categories

import com.gcgenome.rms.dao.CategoryDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.CategoryOrderType
import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.exception.CategoryNotFoundException
import com.gcgenome.rms.exception.CategoryOrderTypeNotFoundException
import com.gcgenome.rms.exception.ServiceNotFoundException
import com.gcgenome.rms.tables.pojos.Category
import com.gcgenome.rms.tables.pojos.Service
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class CategoriesHandler(
    val dslContext: DSLContext
): CategoryDao, ServiceDao {

    fun searchCategories(): Flux<Category> {
        return dslContext.selectCategory()
            .switchIfEmpty(Mono.error(CategoryNotFoundException()))
    }

    fun insertCategories(category: Category): Mono<Category> {
        return checkCategoryOrderType(category)
            .then(dslContext.insertCategory(category))
    }

    fun updateServiceById(service: Service): Mono<Service> {
        return checkServiceById(service.id!!).flatMap {
            dslContext.updateServiceById(service.apply { name = it.name })
        }
    }

    /*fun selectServiceByCategoryId(categoryId: UUID): Mono<Service> {
        return
    }

    fun checkCategoryById(categoryId: UUID): Mono<Category> {
        return dslContext.selectCategory()
    }*/

    fun checkCategoryOrderType(category: Category): Mono<Boolean> {
        val orderType = category.orderType
        return if (orderType != null &&
            orderType in listOf(CategoryOrderType.SINGLE.toString(), CategoryOrderType.MULTIPLE.toString())) {
            Mono.just(true)
        } else {
            Mono.error(CategoryOrderTypeNotFoundException())
        }
    }

    fun checkServiceById(serviceId: String): Mono<Service_> {
        return dslContext.selectServiceById(serviceId)
            .switchIfEmpty(Mono.error(ServiceNotFoundException(serviceId)))
    }
}