package com.gcgenome.rms.dao

import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.records.UserRecord
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

interface UserDao{
    fun DSLContext.selectUserById(userId: String): Mono<UserRecord> {
        return Mono.from(selectFrom(USER).where(USER.ID.eq(userId)))
    }

    fun DSLContext.insertUser(dto: User): Mono<UserRecord> =
        Mono.from(
            insertInto(USER)
                .columns(USER.ID, USER.AUTHORITY, USER.DEPARTMENT, USER.KEY, USER.NAME, USER.PASSWORD, USER.STATE)
                .values(dto.id, "USER", dto.department, UUID.randomUUID(), dto.name, dto.password, "ACTIVE")
                .returning()
        )

    fun DSLContext.deleteUserById(userId: String): Mono<UserRecord> =
        Mono.from(deleteFrom(USER).where(USER.ID.eq(userId)).returning())
}