package com.gcgenome.rms.categories

import com.gcgenome.rms.dao.CategoryDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.data.CategoryOrderType
import com.gcgenome.rms.exception.CategoryNotFoundException
import com.gcgenome.rms.exception.CategoryOrderTypeNotFoundException
import com.gcgenome.rms.tables.pojos.Category
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

    fun checkCategoryOrderType(category: Category): Mono<Boolean> {
        val orderType = category.orderType
        return if (orderType != null &&
            orderType in listOf(CategoryOrderType.SINGLE.toString(), CategoryOrderType.MULTIPLE.toString())) {
            Mono.just(true)
        } else {
            Mono.error(CategoryOrderTypeNotFoundException())
        }
    }
}