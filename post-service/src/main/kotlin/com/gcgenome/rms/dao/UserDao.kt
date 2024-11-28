package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.User
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Flux

interface UserDao {
    //권한이 유저고 활성화가 되어있는 사용자 전부 추출
    fun DSLContext.userPermissionSelectAllUser(userId: String): Flux<User> {
        return Flux.from(selectFrom(USER).where(USER.ROLE.eq("USER")
            .and(USER.STATE.eq("ACTIVE"))))
            .map { it.into(User::class.java) }
    }
    // 권한이 매니저, 어드민이고 활성화가 되어있으며 해당되는 사용자 전부 추출
    fun DSLContext.selectManagerAndUser(userId: String): Flux<User> {
        return Flux.from(selectFrom(USER).where(USER.ROLE.`in`("ADMIN", "MANAGER")
            .and(USER.STATE.eq("ACTIVE"))).or(USER.ID.eq(userId)))
            .map { it.into(User::class.java) }
    }
}