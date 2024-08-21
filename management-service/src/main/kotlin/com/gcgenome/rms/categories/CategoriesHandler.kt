package com.gcgenome.rms.categories

import com.gcgenome.rms.dao.CategoryDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.CategoryOrderType
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.exception.CategoryNotFoundException
import com.gcgenome.rms.exception.CategoryOrderTypeNotFoundException
import com.gcgenome.rms.tables.pojos.Category
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
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

    fun getCategories(categoryId: UUID): Mono<Category> {
        return dslContext.selectCategoryById(categoryId)
            .switchIfEmpty(Mono.error(CategoryNotFoundException()))
    }

    fun insertCategories(category: Category): Mono<Category> {
        return checkCategoryOrderType(category)
            .then(dslContext.insertCategory(category))
    }

    fun deleteCategories(category: Category): Mono<Category> {
        return dslContext.run {
            cancelCategoryByService(category.id!!)
                .then(deleteCategory(category))
        }
    }

    fun updateCategories(category: Category): Mono<Category> {
        return checkCategoryOrderType(category)
            .then(dslContext.updateCategory(category))
    }

    fun selectServiceInfoByCategoryId(categoryId: UUID, filter: Query.Companion.Filter): Flux<Service_> {
        val whereClause = buildServiceIdOrNameWhereClause(filter)
        return Flux.from(checkCategoryById(categoryId)
            .thenMany(dslContext.selectServiceInfoByCategoryId(categoryId, whereClause)))
    }

    fun checkCategoryById(categoryId: UUID): Mono<Category> {
        return dslContext.selectCategoryById(categoryId)
            .switchIfEmpty(Mono.error(CategoryNotFoundException()))
    }

    fun checkCategoryOrderType(category: Category): Mono<Boolean> {
        val orderType = category.orderType
        return if (orderType != null &&
            orderType in listOf(CategoryOrderType.SINGLE.toString(), CategoryOrderType.MULTIPLE.toString())) {
            Mono.just(true)
        } else {
            Mono.error(CategoryOrderTypeNotFoundException())
        }
    }

    fun buildServiceIdOrNameWhereClause(filter: Query.Companion.Filter) : Condition {
        return field("service.id").like("%${filter.value}%").or(field("service.name").like("%${filter.value}%"))
    }
}