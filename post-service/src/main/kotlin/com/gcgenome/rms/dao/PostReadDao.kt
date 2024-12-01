package com.gcgenome.rms.dao

import com.gcgenome.rms.data.PostReadDTO
import com.gcgenome.rms.tables.pojos.PostRead
import com.gcgenome.rms.tables.references.POST
import com.gcgenome.rms.tables.references.POST_CATEGORY
import com.gcgenome.rms.tables.references.POST_READ
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface PostReadDao {
    fun DSLContext.selectPostRead(id: Long): Flux<PostRead> {
        return Flux.from(selectFrom(POST_READ).where(POST_READ.POST_ID.eq(id))
        ).map { it.into(PostRead::class.java) }
    }
    fun DSLContext.insertPostRead(id: Long, userId: String): Mono<PostRead> {
        return Mono.from(
            insertInto(POST_READ)
                .set(POST_READ.POST_ID, id)
                .set(POST_READ.USER_ID, userId)
                .returning()
        ).map { it.into(PostRead::class.java) }
    }

    fun DSLContext.updatePostRead(id: Long, userId: String): Mono<PostRead> {
        return Mono.from(
            update(POST_READ)
                .set(POST_READ.READ_AT, LocalDateTime.now())
                .where(POST_READ.POST_ID.eq(id).and(POST_READ.USER_ID.eq(userId)).and(POST_READ.READ_AT.isNull))
                .returning()
        ).map { it.into(PostRead::class.java) }
    }

    fun DSLContext.changeNullPostRead(id: Long, userId: String?): Mono<PostRead> {
        return Mono.from(
            update(POST_READ)
                .set(POST_READ.READ_AT, null as LocalDateTime?)
                .where(POST_READ.POST_ID.eq(id).and(POST_READ.USER_ID.eq(userId)))
                .returning()
        ).map { it.into(PostRead::class.java) }
    }

    fun DSLContext.selectPostReadByUserId(userId: String): Flux<PostReadDTO> {
        return Flux.from(
            select(
                POST_READ.POST_ID.`as`("post_id"),
                POST_READ.USER_ID.`as`("user_id"),
                POST_READ.READ_AT.`as`("read_at"),
                POST.TITLE.`as`("title"),
                POST.CONTENT.`as`("content"),
                POST.CREATE_AT.`as`("create_at"),
                POST.LAST_MODIFY_AT.`as`("last_modify_at"),
                POST_CATEGORY.NAME.`as`("category")
            )
            .from(POST_READ)
            .join(POST).on(POST_READ.USER_ID.eq(POST_READ.USER_ID).and(POST_READ.POST_ID.eq(POST.ID)))
            .join(POST_CATEGORY).on(POST.POST_CATEGORY_ID.eq(POST_CATEGORY.ID))
            .where(POST_READ.READ_AT.isNull.and(POST_READ.USER_ID.eq(userId)).and(POST_CATEGORY.NAME.notEqual("faq")))
                .orderBy(POST.LAST_MODIFY_AT.desc())
        ).map { it.into(PostReadDTO::class.java) }
    }

    fun DSLContext.getAlarmCountByUserId(userId: String): Mono<Int> {
        return Mono.from(selectCount().from(POST_READ).where(POST_READ.USER_ID.eq(userId).and(POST_READ.READ_AT.isNull)))
            .map { it.component1() }
    }
}