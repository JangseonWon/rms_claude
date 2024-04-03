package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.UpdateUser
import com.gcgenome.rms.data.User
import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.tables.references.USER
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortOrder
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface UserDao{
    fun DSLContext.selectUserById(userId: String): Mono<User> {
        return Mono.from(select(USER.ID, USER.NAME, USER.ROLE,  USER.TYPE, USER.EMAIL, USER.PHONE_NUMBER, USER.KEY, USER.STATE, USER.BRANCH_SERIAL, USER.BRANCH_NAME, USER.CREATE_AT)
            .from(USER).where(USER.ID.eq(userId)))
            .map { it.into(User::class.java) }
    }

    fun DSLContext.selectUsers(query:Query, whereClause:Condition): Flux<User> {
        val order: String = query.sortBy?.let { query.sortBy } ?: "CREATE_AT"

        val asc: SortOrder = when(query.asc) {
            true -> SortOrder.ASC
            false -> SortOrder.DESC
            else -> SortOrder.DEFAULT
        }

        return Flux.from(
            select(
                USER.ID,
                USER.NAME,
                USER.ROLE,
                USER.TYPE,
                USER.EMAIL,
                USER.PHONE_NUMBER,
                USER.KEY, USER.STATE,
                USER.BRANCH_SERIAL,
                USER.BRANCH_NAME,
                USER.CREATE_AT,
                field(
                    select(
                        jsonObject(
                            key("id").value(ORGANIZATION.ID),
                            key("name").value(ORGANIZATION.NAME),
                            key("type").value(ORGANIZATION.TYPE),
                            key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                            key("nursing_number").value(ORGANIZATION.NURSING_NUMBER)
                        )
                    ).from(ORGANIZATION)
                        .where(USER.ID.eq(ORGANIZATION.USER_ID).and(USER.ID.eq(ORGANIZATION.ID)))
                ).`as`("organization"))
            .from(USER)
                .where(whereClause)
                .orderBy(field(order).sort(asc))
                .limit(query.size)
                .offset(query.page*query.size)
        ).map { it.into(User::class.java) }
    }

    fun DSLContext.selectUsersCount(query: Query, whereClause:Condition): Mono<Int> {
        return Mono.from(
            selectCount().from(USER)
                .where(whereClause)
        ).map { it.component1() }
    }

    fun DSLContext.selectUserOrganizationById(userDto: User): Mono<User> {
        return Mono.from(select(USER.ID, USER.NAME, USER.ROLE, USER.TYPE, USER.EMAIL, USER.PHONE_NUMBER, USER.KEY, USER.STATE, USER.BRANCH_SERIAL, USER.BRANCH_NAME, USER.CREATE_AT,
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
                .set(USER.CREATE_AT,LocalDateTime.now())
                .returning()
        ).map{it.into(User::class.java)}
    }

    fun DSLContext.updateUserById(userId: String, password: String?, userDto: UpdateUser): Mono<User> {
        return Mono.from(
            update(USER)
                .set(USER.NAME, DSL.coalesce(DSL.`val`(userDto.name), USER.NAME))
                .set(USER.PASSWORD, DSL.coalesce(DSL.`val`(password), USER.PASSWORD))
                .set(USER.ROLE, DSL.coalesce(DSL.`val`(userDto.role), USER.ROLE))
                .set(USER.TYPE, DSL.coalesce(DSL.`val`(userDto.type), USER.TYPE))
                .set(USER.EMAIL, DSL.coalesce(DSL.`val`(userDto.email), USER.EMAIL))
                .set(USER.PHONE_NUMBER, DSL.coalesce(DSL.`val`(userDto.phoneNumber), USER.PHONE_NUMBER))
                .set(USER.STATE, DSL.coalesce(DSL.`val`(userDto.state), USER.STATE))
                .where(USER.ID.eq(userId))
                .returningResult(USER.ID,USER.NAME,USER.ROLE,USER.TYPE,USER.EMAIL,USER.PHONE_NUMBER,USER.KEY,USER.STATE,USER.BRANCH_SERIAL,USER.BRANCH_NAME,USER.CREATE_AT)
        ).map{it.into(User::class.java)}
    }
}