package com.gcgenome.rms.dao

import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.records.UserRecord
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserDao{
    fun DSLContext.selectUsers(): Flux<UserRecord> {
        return Flux.from(selectFrom(USER).orderBy(USER.ID))
    }
    fun DSLContext.selectUserById(userId: String): Mono<UserRecord> {
        return Mono.from(selectFrom(USER).where(USER.ID.eq(userId)))
    }
    fun DSLContext.updateUserById(userId: String, userDto: User): Mono<UserRecord> {
        return Mono.from(
            update(USER)
                .set(USER.AUTHORITY, userDto.authority)
                .set(USER.DEPARTMENT, userDto.department)
                .set(USER.KEY, userDto.key)
                .set(USER.NAME, userDto.name)
                .set(USER.STATE, userDto.state)
                .set(USER.CODE, userDto.code)
                .set(USER.PASSWORD, userDto.password)
                .where(USER.ID.eq(userId))
                .returning())
    }
    fun DSLContext.insertUser(userDto: User): Mono<UserRecord> {
        return Mono.from(
            insertInto(USER)
                .set(USER.ID, userDto.id)
                .set(USER.AUTHORITY, userDto.authority)
                .set(USER.DEPARTMENT, userDto.department)
                .set(USER.KEY, userDto.key)
                .set(USER.NAME, userDto.name)
                .set(USER.PASSWORD, userDto.password)
                .set(USER.STATE, userDto.state)
                .set(USER.CODE, userDto.code)
                .returning()
        )
    }

}