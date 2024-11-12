package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.User
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Flux

interface UserDao {
    fun DSLContext.userPermissionsSelectUser(userId: String): Flux<User> {
        return Flux.from(selectFrom(USER).where(USER.ROLE.`in`("ADMIN", "MANAGER")
            .and(USER.STATE.eq("ACTIVE"))).or(USER.ID.eq(userId)))
            .map { it.into(User::class.java) }
    }
}