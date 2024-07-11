package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Service_
import com.gcgenome.rms.tables.pojos.Category
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
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

    fun DSLContext.deleteCategory(category: Category): Mono<Category> {
        return Mono.from(
            deleteFrom(CATEGORY).where(CATEGORY.ID.eq(category.id))
                .returning()
        ).map { it.into(Category::class.java) }
    }

    fun DSLContext.updateCategory(category: Category): Mono<Category> {
        return Mono.from(
            update(CATEGORY)
                .set(CATEGORY.NAME, category.name)
                .set(CATEGORY.ORDER_TYPE, category.orderType)
                .where(CATEGORY.ID.eq(category.id))
                .returning()
        ).map { it.into(Category::class.java) }
    }

    fun DSLContext.selectCategoryById(categoryId: UUID): Mono<Category> {
        return Mono.from(
            selectFrom(CATEGORY).where(CATEGORY.ID.eq(categoryId))
        ).map { it.into(Category::class.java) }
    }

    fun DSLContext.selectServiceInfoByCategoryId(categoryId: UUID, whereClause: Condition): Flux<Service_> {
        return Flux.from(
            select(
                SERVICE.ID,
                SERVICE.NAME,
                `when`(
                    count(SERVICE_EXTENSION.EXTENSION_ID).greaterThan(0),
                    jsonArrayAgg(
                        jsonObject(
                            key("id").value(EXTENSION.ID),
                            key("name").value(EXTENSION.NAME),
                            key("regex").value(EXTENSION.REGEX),
                            key("required").value(SERVICE_EXTENSION.REQUIRED),
                        )
                    )
                ).`as`("extensions"),
                field(
                    select(
                        jsonArrayAgg(
                            jsonObject(
                                key("id").value(SAMPLE_TYPE.ID),
                                key("name").value(SAMPLE_TYPE.NAME)
                            )
                        )
                    ).from(SAMPLE_TYPE)
                        .join(SERVICE_SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                        .where(SERVICE.ID.eq(SERVICE_SAMPLE_TYPE.SERVICE_ID))
                ).`as`("sample_types")
            ).from(SERVICE)
                .join(CATEGORY).on(CATEGORY.ID.eq(SERVICE.CATEGORY_ID))
                .leftJoin(SERVICE_EXTENSION).on(SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID))
                .leftJoin(EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                .where(SERVICE.CATEGORY_ID.eq(categoryId).and(whereClause))
                .groupBy(SERVICE.ID, SERVICE.NAME)
        ).map { it.into(Service_::class.java) }
    }
}