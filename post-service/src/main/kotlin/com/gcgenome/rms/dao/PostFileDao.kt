package com.gcgenome.rms.dao

import com.gcgenome.rms.data.PostFileDTO
import com.gcgenome.rms.tables.references.POST_FILE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface PostFileDao {
    fun DSLContext.selectPostFileById(fileId: UUID): Mono<PostFileDTO> {
        return Mono.from(
            selectFrom(POST_FILE).where(POST_FILE.ID.eq(fileId))
        ).map { it.into(PostFileDTO::class.java) }
    }
    fun DSLContext.insertPostFile(postFile: PostFileDTO): Mono<PostFileDTO> {
        return Mono.from(
            insertInto(POST_FILE)
                .set(POST_FILE.ID, UUID.randomUUID())
                .set(POST_FILE.PATH, postFile.path)
                .set(POST_FILE.CREATE_AT, postFile.createAt ?: LocalDateTime.now())
                .set(POST_FILE.NAME, postFile.name)
                .set(POST_FILE.POST_ID, postFile.post!!.id)
                .returning()
        ).map { it.into(PostFileDTO::class.java) }
    }

    fun DSLContext.deletePostFileById(id: UUID): Mono<PostFileDTO> {
        return Mono.from(
            deleteFrom(POST_FILE)
                .where(POST_FILE.ID.eq(id))
                .returning()
        ).map { it.into(PostFileDTO::class.java) }
    }
    fun DSLContext.deletePostFileByPostId(postId: UUID): Flux<PostFileDTO> {
        return Flux.from(
            deleteFrom(POST_FILE)
                .where(POST_FILE.POST_ID.eq(postId))
                .returning()
        ).map { it.into(PostFileDTO::class.java) }
    }
}