package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.PostFile
import com.gcgenome.rms.tables.references.POST_FILE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface PostFileDao {
    fun DSLContext.insertPostFile(postFile: PostFile): Mono<PostFile> {
        return Mono.from(
            insertInto(POST_FILE)
                .set(POST_FILE.ID, UUID.randomUUID())
                .set(POST_FILE.PATH, postFile.path)
                .set(POST_FILE.CREATE_AT, postFile.createAt ?: LocalDateTime.now())
                .set(POST_FILE.NAME, postFile.name)
                .set(POST_FILE.POST_ID, postFile.postId)
                .returning()
        ).map { it.into(PostFile::class.java) }
    }

    fun DSLContext.deletePostFile(postId: UUID): Mono<PostFile> {
        return Mono.from(
            deleteFrom(POST_FILE)
                .where(POST_FILE.POST_ID.eq(postId))
                .returning()
        ).map { it.into(PostFile::class.java) }
    }
}