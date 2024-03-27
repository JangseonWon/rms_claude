package com.gcgenome.rms.dao

import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface Dao {
    fun DSLContext.selectUserById(userId: String): Mono<User> {
        return Mono.from(selectFrom(USER).where(USER.ID.eq(userId)))
            .map (User::toModel)
    }
}