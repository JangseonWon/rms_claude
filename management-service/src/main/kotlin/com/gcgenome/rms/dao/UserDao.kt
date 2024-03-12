package com.gcgenome.rms.dao

import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import org.jooq.impl.DSL.jsonObject
import org.jooq.impl.DSL.key
import reactor.core.publisher.Mono
import java.util.*

interface UserDao{
    fun DSLContext.selectUserById(userId: String): Mono<User> {
        return Mono.from(select(USER.ID, USER.NAME, USER.ROLE,  USER.TYPE, USER.EMAIL, USER.PHONE_NUMBER, USER.KEY, USER.STATE, USER.BRANCH_SERIAL, USER.BRANCH_NAME)
            .from(USER).where(USER.ID.eq(userId)))
            .map { it.into(User::class.java) }
    }

    fun DSLContext.selectUserOrganizationById(userDto: User): Mono<User> {
        return Mono.from(select(USER.ID, USER.NAME, USER.ROLE, USER.TYPE, USER.EMAIL, USER.PHONE_NUMBER, USER.KEY, USER.STATE, USER.BRANCH_SERIAL, USER.BRANCH_NAME,
            jsonObject(
                key("id").value(ORGANIZATION.ID),
                key("name").value(ORGANIZATION.NAME),
                key("type").value(ORGANIZATION.TYPE),
                key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
            ).`as`("organization"))
            .from(USER, ORGANIZATION)
            .where(USER.ID.eq(userDto.id)).and(USER.ID.eq(ORGANIZATION.USER_ID)).and(ORGANIZATION.ID.eq(userDto.organization!!.id)))
            .map { it.into(User::class.java) }
    }

    fun DSLContext.insertUser(userDto: User, password: String?): Mono<User> {
        return Mono.from(
            insertInto(USER)
                .set(USER.ID, userDto.id)
                .set(USER.NAME, userDto.name)
                .set(USER.PASSWORD, password)
                .set(USER.ROLE, userDto.role)
                .set(USER.TYPE, userDto.type)
                .set(USER.EMAIL,userDto.email)
                .set(USER.PHONE_NUMBER,userDto.phoneNumber)
                .set(USER.KEY, UUID.randomUUID())
                .set(USER.STATE, userDto.state)
                .set(USER.BRANCH_SERIAL, userDto.branchSerial)
                .set(USER.BRANCH_NAME, userDto.branchName)
                .returning()
        ).map{it.into(User::class.java)}
    }
}