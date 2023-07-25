package com.gcgenome.rms.service

import com.gcgenome.lims.tables.User.Companion.USER
import com.gcgenome.rms.data.User

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.service.UserDao")
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
}
