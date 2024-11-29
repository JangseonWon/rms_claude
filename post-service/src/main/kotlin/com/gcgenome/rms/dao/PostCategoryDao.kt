package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.PostCategory
import com.gcgenome.rms.tables.references.POST_CATEGORY
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface PostCategoryDao {
    fun DSLContext.getCategoryById(categoryId: UUID): Mono<PostCategory> {
        return Mono.from(selectFrom(POST_CATEGORY).where(POST_CATEGORY.ID.eq(categoryId)))
            .map{it.into(PostCategory::class.java)}
    }
}