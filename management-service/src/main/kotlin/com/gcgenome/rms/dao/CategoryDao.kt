package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.Category
import com.gcgenome.rms.tables.references.CATEGORY
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface CategoryDao {
    fun DSLContext.selectCategory(): Flux<Category> {
        return Flux.from(selectFrom(CATEGORY)).map { it.into(Category::class.java) }
    }

    fun DSLContext.insertCategory(category: Category): Mono<Category> {
        return Mono.from(
            insertInto(CATEGORY)
                .set(CATEGORY.ID, UUID.randomUUID())
                .set(CATEGORY.NAME, category.name)
                .set(CATEGORY.ORDER_TYPE, category.orderType)
                .returning()
        ).map { it.into(Category::class.java) }
    }
}