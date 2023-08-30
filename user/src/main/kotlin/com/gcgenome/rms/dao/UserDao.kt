package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserDao{
    fun DSLContext.selectUsers(query: Query): Flux<User> {
        return Flux.from(select(USER.ID, USER.AUTHORITY, USER.KEY, USER.NAME, USER.STATE, USER.CODE)
            .from(USER)
            .orderBy(USER.ID)
            .limit(query.size)
            .offset(query.page*query.size)
        ).map{it.into(User::class.java)}
    }
    fun DSLContext.selectUsersCount(query: Query): Mono<Int> {
        return Mono.from(selectCount().from(USER)).map { it.component1() }
    }
    fun DSLContext.selectUserById(userId: String): Mono<User> {
        return Mono.from(select(USER.ID, USER.AUTHORITY, USER.KEY, USER.NAME, USER.STATE, USER.CODE).from(USER).where(USER.ID.eq(userId)))
            .map { it.into(User::class.java) }
    }
    fun DSLContext.updateUserById(userId: String, userDto: User): Mono<User> {
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
                .returning()
        ).map{it.into(User::class.java)}
    }
    fun DSLContext.insertUser(userDto: User): Mono<User> {
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
        ).map{it.into(User::class.java)}
    }

}