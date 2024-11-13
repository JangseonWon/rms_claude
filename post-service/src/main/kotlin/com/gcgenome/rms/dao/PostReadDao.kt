package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.PostRead
import com.gcgenome.rms.tables.references.POST_READ
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface PostReadDao {
    fun DSLContext.selectPostRead(id: Long, userId: String): Mono<PostRead> {
        return Mono.from(selectFrom(POST_READ).where(POST_READ.POST_ID.eq(id).and(POST_READ.USER_ID.eq(userId)))
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

    fun DSLContext.updatePostRead(id: Long, userId: String, dateTime: LocalDateTime?): Mono<PostRead> {
        return Mono.from(
            update(POST_READ)
                .set(POST_READ.READ_AT, dateTime)
                .where(POST_READ.USER_ID.eq(userId))
                .returning()
        ).map { it.into(PostRead::class.java) }
    }
}