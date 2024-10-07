package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.PostFile
import com.gcgenome.rms.tables.references.POST_FILE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface FileDao {
    fun DSLContext.getFileByPostId(postId: UUID): Mono<PostFile> {
        return Mono.from(
            selectFrom(POST_FILE)
                .where(POST_FILE.POST_ID.eq(postId))
        ).map { it.into(PostFile::class.java) }
    }

    fun DSLContext.insertFile(file: PostFile): Mono<PostFile> {
        return Mono.from(
            insertInto(POST_FILE)
                .set(POST_FILE.ID, file.id)
                .set(POST_FILE.NAME, file.name)
                .set(POST_FILE.CREATE_AT, file.createAt ?: LocalDateTime.now())
                .set(POST_FILE.PATH, file.path)
                .set(POST_FILE.POST_ID, file.postId)
                .returning()
        ).map { it.into(PostFile::class.java) }
    }

    fun DSLContext.deleteFileById(fileId: UUID): Mono<PostFile> {
        return Mono.from(
            deleteFrom(POST_FILE)
                .where(POST_FILE.ID.eq(fileId))
                .returning()
        ).map { it.into(PostFile::class.java) }
    }

    fun DSLContext.deleteFileByPostId(postId: UUID): Mono<PostFile> {
        return Mono.from(
            deleteFrom(POST_FILE)
                .where(POST_FILE.POST_ID.eq(postId))
                .returning()
        ).map { it.into(PostFile::class.java) }
    }
}