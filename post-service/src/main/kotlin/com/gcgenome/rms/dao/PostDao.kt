package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Post_
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.references.COMMENT
import com.gcgenome.rms.tables.references.POST
import com.gcgenome.rms.tables.references.POST_FILE
import com.gcgenome.rms.tables.references.USER
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortOrder
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


interface PostDao{
    fun DSLContext.selectPostAll(query: Query, whereClause: Condition?): Flux<Post_> {
        val asc: SortOrder = when(query.asc) {
            true -> SortOrder.ASC
            false -> SortOrder.DESC
            else -> SortOrder.DEFAULT
        }

        return Flux.from(
            select(
                POST.ID,
                POST.TITLE,
                POST.CONTENT,
                POST.CREATE_AT,
                POST.LAST_MODIFY_AT,
                POST.READ,
                POST.POST_CATEGORY_ID,
                field(
                    select(
                        jsonObject(
                            key("id").value(USER.ID),
                            key("name").value(USER.NAME)
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
                                key("post_id").value(POST_FILE.POST_ID),
                            )
                        )
                    ).from(POST_FILE)
                        .where(POST_FILE.POST_ID.eq(POST.ID))
                ).`as`("files"),
                field(
                    select(
                        jsonArrayAgg(
                            jsonObject(
                                key("id").value(COMMENT.ID),
                                key("content").value(COMMENT.CONTENT),
                                key("create_at").value(COMMENT.CREATE_AT),
                                key("last_modify_at").value(COMMENT.LAST_MODIFY_AT),
                                key("user_id").value(COMMENT.USER_ID),
                                key("post_id").value(COMMENT.POST_ID),
                            )
                        )
                    ).from(COMMENT)
                        .where(COMMENT.POST_ID.eq(POST.ID))
                ).`as`("comments"),
            )
                .from(POST)
                .where(whereClause)
                .orderBy(POST.CREATE_AT.sort(asc))
                .limit(query.size)
                .offset(query.page*query.size)
        ).map { it.into(Post_::class.java) }
    }

    fun DSLContext.selectPostById(postId: UUID): Mono<Post_> {
        return Mono.from(
            select(
                POST.ID,
                POST.TITLE,
                POST.CONTENT,
                POST.CREATE_AT,
                POST.LAST_MODIFY_AT,
                POST.READ,
                POST.POST_CATEGORY_ID,
                field(
                    select(
                        jsonObject(
                            key("id").value(USER.ID),
                            key("name").value(USER.NAME)
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
                                key("post_id").value(POST_FILE.POST_ID),
                            )
                        )
                    ).from(POST_FILE)
                        .where(POST_FILE.POST_ID.eq(POST.ID))
                ).`as`("files"),
                field(
                    select(
                        jsonArrayAgg(
                            jsonObject(
                                key("id").value(COMMENT.ID),
                                key("content").value(COMMENT.CONTENT),
                                key("create_at").value(COMMENT.CREATE_AT),
                                key("last_modify_at").value(COMMENT.LAST_MODIFY_AT),
                                key("user_id").value(COMMENT.USER_ID),
                                key("post_id").value(COMMENT.POST_ID),
                            )
                        ).orderBy(COMMENT.CREATE_AT.asc())
                    ).from(COMMENT)
                        .where(COMMENT.POST_ID.eq(POST.ID))
                ).`as`("comments"),
            )
                .from(POST)
                .where(POST.ID.eq(postId))
        ).map { it.into(Post_::class.java) }
    }

    fun DSLContext.selectPostCount(query: Query, whereClause: Condition?): Mono<Int> {
        return Mono.from(
            select(count())
                .from(POST).where(whereClause)
        ).map { it.component1() }
    }
}