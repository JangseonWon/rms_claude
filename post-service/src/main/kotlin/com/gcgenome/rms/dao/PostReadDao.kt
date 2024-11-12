package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.PostRead
import com.gcgenome.rms.tables.references.POST_READ
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface PostReadDao {
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
                .where(POST_READ.USER_ID.eq(userId))
                .returning()
        ).map { it.into(PostRead::class.java) }
    }
}