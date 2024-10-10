package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.tables.references.ORGANIZATION
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
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

    fun DSLContext.selectUserWithOrganizations(userId: String, query: Query): Mono<Page<UserDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(ORGANIZATION, USER.ID.eq(ORGANIZATION.USER_ID), QueryDao.JoinType.LEFT),
        )
        val fields = listOf(
            USER.ID.`as`("id"),
            USER.NAME.`as`("name"),
            jsonArrayAgg(
                jsonObject(
                    key("id").value(ORGANIZATION.ID),
                    key("name").value(ORGANIZATION.NAME),
                    key("nursing_number").value(ORGANIZATION.NURSING_NUMBER),
                    key("registration_number").value(ORGANIZATION.REGISTRATION_NUMBER),
                    key("type").value(ORGANIZATION.TYPE)
                )
            ).`as`("organizations")
        )
        val where = USER.ID.eq(userId)
        val groupByFields = listOf(USER.ID)

        return selectPage(mainTable = USER, query = query, selectFields = fields, where = where, joinTables = joins, groupByFields = groupByFields) { record ->
            record.into(UserDTO::class.java)
        }
    }
}