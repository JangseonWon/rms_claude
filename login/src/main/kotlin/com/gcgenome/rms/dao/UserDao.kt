package com.gcgenome.rms.dao

import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.records.UserRecord
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface UserDao{
    fun DSLContext.selectUserById(userId: String): Mono<UserRecord> {
        return Mono.from(selectFrom(USER).where(USER.ID.eq(userId)))
    }

    fun DSLContext.insertUser(user: User): Mono<UserRecord> =
        Mono.from(
            insertInto(USER)
                .set(USER.ID, user.id)
                .set(USER.AUTHORITY, "USER")
                .set(USER.STATE, "ACTIVE")
                .returning()
        )
}