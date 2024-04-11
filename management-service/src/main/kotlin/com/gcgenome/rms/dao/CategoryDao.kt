package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.Category
import com.gcgenome.rms.tables.references.CATEGORY
import org.jooq.DSLContext
import reactor.core.publisher.Flux

interface CategoryDao {
    fun DSLContext.selectCategory(): Flux<Category> {
        return Flux.from(selectFrom(CATEGORY)).map { it.into(Category::class.java) }
    }
}