package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.USER
import com.gcgenome.rms.data.User_
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.UserDao")
class UserDao(
    private val dslContext: DSLContext,
){
    fun selectUser(userId: String): Mono<User_> =
        Mono.from(dslContext.selectFrom(USER).where(USER.ID.eq(userId)))
        .map {
            User_(
                id = it.get(USER.ID)!!,
                authority = it.get(USER.AUTHORITY)!!,
                department = it.get(USER.DEPARTMENT),
                key = it.get(USER.KEY)!!,
                name = it.get(USER.NAME)!!,
                state = it.get(USER.STATE)!!
            )
        }
}
