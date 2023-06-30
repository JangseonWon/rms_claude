package com.gcgenome.rms.dao

import com.gcgenome.lims.tables.records.UserRecord
import com.gcgenome.lims.tables.references.USER
import com.gcgenome.rms.data.User
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Repository("com.gcgenome.rms.order.UserDao")
class UserDao(
    private val dslContext: DSLContext,
){
    fun selectUser(userId: String): Mono<User> =
        Mono.from(dslContext.selectFrom(USER).where(USER.ID.eq(userId)))
            .map {
                User(
                    id = it.get(USER.ID)!!,
                    authority = it.get(USER.AUTHORITY)!!,
                    department = it.get(USER.DEPARTMENT),
                    key = it.get(USER.KEY)!!,
                    name = it.get(USER.NAME)!!,
                    state = it.get(USER.STATE)!!
                )
            }

    fun insertUser(dto: User): Mono<UserRecord> =
        Mono.from(
            dslContext.insertInto(USER)
                .columns(USER.ID, USER.AUTHORITY, USER.DEPARTMENT, USER.KEY, USER.NAME, USER.PASSWORD, USER.STATE)
                .values(dto.id, "USER", dto.department, UUID.randomUUID(), dto.name, dto.password, "ACTIVE")
                .returning()
        )

    fun deleteUser(userId: String): Mono<UserRecord> =
        Mono.from(
            dslContext.dsl().deleteFrom(USER).where(USER.ID.eq(userId)).returning()
        )
}