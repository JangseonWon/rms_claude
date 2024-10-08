package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.PostDTO
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.pojos.Post
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*


interface PostDao: QueryDao{

    fun DSLContext.selectPostsWithPage(query: Query): Mono<Page<PostDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(POST_CATEGORY, POST.POST_CATEGORY_ID.eq(POST_CATEGORY.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(USER, POST.USER_ID.eq(USER.ID), QueryDao.JoinType.LEFT),
        )
        val fields = listOf(
            POST.ID.`as`("id"),
            POST.TITLE.`as`("title"),
            POST.CONTENT.`as`("content"),
            POST.CREATE_AT.`as`("create_at"),
            POST.LAST_MODIFY_AT.`as`("last_modify_at"),
            POST.READ.`as`("read"),
            `when`(POST_CATEGORY.ID.isNotNull,
                jsonObject(
                    key("id").value(POST_CATEGORY.ID),
                    key("name").value(POST_CATEGORY.NAME),
                )
            ).`as`("post_category"),
            `when`(USER.ID.isNotNull,
                jsonObject(
                    key("id").value(USER.ID),
                    key("name").value(USER.NAME),
                    key("role").value(USER.ROLE),
                    key("type").value(USER.TYPE),
                    key("email").value(USER.EMAIL),
                    key("phone_number").value(USER.PHONE_NUMBER),
                    key("state").value(USER.STATE),
                    key("branch_serial").value(USER.BRANCH_SERIAL),
                    key("branch_name").value(USER.BRANCH_NAME),
                    key("create_at").value(USER.CREATE_AT)
                )
            ).`as`("user"),
        )
        return selectPage(mainTable = POST, query = query, joinTables = joins, selectFields = fields) {record ->
            record.into(PostDTO::class.java)
        }
    }

    fun DSLContext.selectPostById(postId: UUID): Mono<PostDTO> {
        return Mono.from(
            select(
                POST.ID,
                POST.TITLE,
                POST.CONTENT,
                POST.CREATE_AT,
                POST.LAST_MODIFY_AT,
                POST.READ,
                field(
                    select(
                        jsonObject(
                            key("id").value(USER.ID),
                            key("name").value(USER.NAME),
                            key("role").value(USER.ROLE),
                            key("type").value(USER.TYPE),
                            key("email").value(USER.EMAIL),
                            key("phone_number").value(USER.PHONE_NUMBER),
                            key("state").value(USER.STATE),
                            key("branch_serial").value(USER.BRANCH_SERIAL),
                            key("branch_name").value(USER.BRANCH_NAME),
                            key("create_at").value(USER.CREATE_AT)
                        )
                    ).from(USER)
                        .where(USER.ID.eq(POST.USER_ID))
                ).`as`("user"),
                field(
                    select(
                        jsonArrayAgg(
                            jsonObject(
                                key("id").value(POST_FILE.ID),
                                key("name").value(POST_FILE.NAME),
                                key("path").value(POST_FILE.PATH),
                                key("create_at").value(POST_FILE.CREATE_AT),
                            )
                        )
                    ).from(POST_FILE).where(POST_FILE.POST_ID.eq(POST.ID))
                ).`as`("post_files"),
                field(
                    select(
                        jsonArrayAgg(
                            jsonObject(
                                key("id").value(COMMENT.ID),
                                key("content").value(COMMENT.CONTENT),
                                key("create_at").value(COMMENT.CREATE_AT),
                                key("last_modify_at").value(COMMENT.LAST_MODIFY_AT),
                                key("user").value(
                                    jsonObject(
                                        key("id").value(USER.ID),
                                        key("name").value(USER.NAME),
                                        key("role").value(USER.ROLE)
                                    )
                                )
                            )
                        ).orderBy(COMMENT.CREATE_AT.asc())
                    ).from(COMMENT)
                        .join(USER).on(COMMENT.USER_ID.eq(USER.ID))
                        .where(COMMENT.POST_ID.eq(POST.ID))
                ).`as`("comments")
            )
                .from(POST)
                .where(POST.ID.eq(postId))
        ).map { it.into(PostDTO::class.java) }
    }

    fun DSLContext.selectPostCount(query: Query, whereClause: Condition?): Mono<Int> {
        return Mono.from(
            select(count())
                .from(POST).where(whereClause)
        ).map { it.component1() }
    }

    fun DSLContext.insertPost(post: PostDTO): Mono<PostDTO> {
        return Mono.from(
            insertInto(POST)
                .set(POST.ID, UUID.randomUUID())
                .set(POST.TITLE, post.title)
                .set(POST.CONTENT, post.content)
                .set(POST.CREATE_AT, LocalDateTime.now())
                .set(POST.LAST_MODIFY_AT, LocalDateTime.now())
                .set(POST.POST_CATEGORY_ID, post.postCategory!!.id)
                .set(POST.USER_ID, post.user!!.id)
                .set(POST.READ, false)
                .returning()
        ).map { it.into(PostDTO::class.java) }
    }

    fun DSLContext.updatePost(post: PostDTO): Mono<PostDTO> {
        return Mono.from(
            update(POST)
                .set(POST.TITLE, post.title)
                .set(POST.CONTENT, post.content)
                .set(POST.LAST_MODIFY_AT, LocalDateTime.now())
                .set(POST.READ, false)
                .where(POST.ID.eq(post.id))
                .returning()
        ).map { it.into(PostDTO::class.java) }
    }

    fun DSLContext.deletePostByPostId(postId: UUID): Mono<Post> {
        return Mono.from(
            deleteFrom(POST).where(POST.ID.eq(postId))
                .returning()
        ).map { it.into(Post::class.java) }
    }

    fun DSLContext.readChangeByPostId(postId: UUID, new: Boolean): Mono<Post> {
        return Mono.from(
            update(POST)
                .set(POST.READ, new)
                .where(POST.ID.eq(postId))
                .returning()
        ).map { it.into(Post::class.java) }
    }
}