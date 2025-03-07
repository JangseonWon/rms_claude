package com.gcgenome.rms.catalog.categories

import com.gcgenome.rms.dao.CategoryDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.exceptions.CategoryNotFoundException
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
}