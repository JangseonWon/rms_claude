package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.Category
import com.gcgenome.rms.tables.references.CATEGORY
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface CategoryDao {

    fun DSLContext.selectCategoryById(categoryId: UUID): Mono<Category> {
        return Mono.from(
            selectFrom(CATEGORY).where(CATEGORY.ID.eq(categoryId))
        ).map { it.into(Category::class.java) }
    }

}