package com.gcgenome.rms.dao

import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import org.jooq.impl.DSL.coalesce
import org.jooq.impl.DSL.`val`
import reactor.core.publisher.Mono

interface UserDao : QueryDao{
    fun DSLContext.selectUserById(userId: String): Mono<UserDTO> {
        return Mono.from(
            selectFrom(USER)
            .where(USER.ID.eq(userId)))
            .map { it.into(UserDTO::class.java) }
    }

    fun DSLContext.updateUserById(user: UserDTO): Mono<UserDTO> {
        return Mono.from(
            update(USER)
                .set(USER.NAME, coalesce(`val`(user.name), USER.NAME))
                .set(USER.PASSWORD, coalesce(`val`(user.password), USER.PASSWORD))
                .set(USER.ROLE, coalesce(`val`(user.role), USER.ROLE))
                .set(USER.TYPE, coalesce(`val`(user.type), USER.TYPE))
                .set(USER.EMAIL, coalesce(`val`(user.email), USER.EMAIL))
                .set(USER.PHONE_NUMBER, coalesce(`val`(user.phoneNumber), USER.PHONE_NUMBER))
                .set(USER.STATE, coalesce(`val`(user.state), USER.STATE))
                .where(USER.ID.eq(user.id))
                .returningResult(USER.ID,USER.NAME,USER.ROLE,USER.TYPE,USER.EMAIL,USER.PHONE_NUMBER,USER.STATE,USER.BRANCH_SERIAL,USER.BRANCH_NAME,USER.CREATE_AT)
        ).map{it.into(UserDTO::class.java)}
    }
}