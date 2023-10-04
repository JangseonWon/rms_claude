package com.gcgenome.rms.dao

import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface UserDao{
    fun DSLContext.selectUserById(userId: String): Mono<User> {
        return Mono.from(selectFrom(USER).where(USER.ID.eq(userId)))
            .map { it.into(User::class.java) }
    }
    fun DSLContext.selectUserByKey(key: UUID): Mono<User> {
        return Mono.from(selectFrom(USER).where(USER.KEY.eq(key)))
            .map { it.into(User::class.java) }
    }
}
