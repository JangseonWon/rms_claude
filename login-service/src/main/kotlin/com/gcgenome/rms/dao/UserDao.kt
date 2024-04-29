package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.User
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

interface UserDao{
    fun DSLContext.selectUserById(userId: String): Mono<User> {
        return Mono.from(selectFrom(USER).where(USER.ID.eq(userId)))
            .map { it.into(User::class.java) }
    }
    fun DSLContext.insertUser(dto: User): Mono<User> {
        return Mono.from(
            insertInto(USER)
                .set(USER.ID, dto.id)
                .set(USER.NAME, dto.name)
                .set(USER.PASSWORD, dto.password)
                .set(USER.ROLE, dto.role)
                .set(USER.TYPE, dto.type)
                .set(USER.EMAIL, dto.email)
                .set(USER.KEY, dto.key)
                .set(USER.STATE, dto.state)
                .set(USER.BRANCH_SERIAL, dto.branchSerial)
                .set(USER.BRANCH_NAME, dto.branchName)
                .set(USER.CREATE_AT, LocalDateTime.now())
                .returning(USER.ID, USER.NAME, USER.ROLE, USER.TYPE, USER.EMAIL, USER.KEY, USER.STATE,USER.BRANCH_SERIAL, USER.BRANCH_NAME, USER.CREATE_AT)
        ).map { it.into(User::class.java) }
    }
}