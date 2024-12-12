package com.gcgenome.rms.dao

import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.pojos.User
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.USER
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface UserDao : QueryDao{
    fun DSLContext.upsertUsers(alisOrganization: AlisOrganization, pwd: String): Mono<UserDTO> {
        return Mono.from(
            insertInto(USER)
                .set(USER.ID, alisOrganization.compCode)
                .set(USER.NAME, alisOrganization.compName)
                .set(USER.PASSWORD, pwd)
                .set(USER.ROLE, Role.USER.toString())
                .set(USER.TYPE, UserType.GENERAL_INSTITUTION.toString())
                .set(USER.STATE, UserState.INACTIVE.toString())
                .set(USER.BRANCH_SERIAL, alisOrganization.compMngBeginNo)
                .set(USER.BRANCH_NAME, alisOrganization.compMngName)
                .set(USER.CREATE_AT, LocalDateTime.now())
                .onConflict(USER.ID)
                .doUpdate()
                .set(USER.NAME, alisOrganization.compName)
                .set(USER.BRANCH_SERIAL, alisOrganization.compMngBeginNo)
                .set(USER.BRANCH_NAME, alisOrganization.compMngName)
                .returning()
        ).map { it.into(UserDTO::class.java) }
    }

    fun DSLContext.insertManager(dto: User): Mono<UserDTO> {
        return Mono.from(
            insertInto(USER)
                .set(USER.ID, dto.id)
                .set(USER.NAME, dto.name)
                .set(USER.PASSWORD, dto.password)
                .set(USER.ROLE, "MANAGER")
                .set(USER.TYPE, "TRUSTEE")
                .set(USER.EMAIL, dto.email)
                .set(USER.KEY, UUID.randomUUID())
                .set(USER.STATE, "ACTIVE")
                .set(USER.BRANCH_SERIAL, dto.branchSerial)
                .set(USER.BRANCH_NAME, dto.branchName)
                .set(USER.PHONE_NUMBER, dto.phoneNumber)
                .set(USER.CREATE_AT, LocalDateTime.now())
                .returning()
        ).map { it.into(UserDTO::class.java) }
    }

    fun DSLContext.selectUserWithServicesQuery(userId: String, query: Query): Mono<UserDTO> {
        val joins = listOf(
            QueryDao.JoinInfo(USER_SERVICE, USER.ID.eq(USER_SERVICE.USER_ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SERVICE, USER_SERVICE.SERVICE_ID.eq(SERVICE.ID), QueryDao.JoinType.LEFT)
        )
        val fields = listOf(
            USER.ID.`as`("id"),
            USER.NAME.`as`("name"),
            jsonArrayAgg(
                jsonObject(
                    key("id").value(SERVICE.ID),
                    key("name").value(SERVICE.NAME)
                )
            ).`as`("services")
        )
        val where = USER.ID.eq(userId)
        val groupByFields = listOf(USER.ID)

        return selectQuery(mainTable = USER, query = query, selectFields = fields, where = where, joinTables = joins, groupByFields = groupByFields) { record ->
            record.into(UserDTO::class.java)
        }
    }
    fun DSLContext.selectUsersWithPage(query: Query): Mono<Page<UserDTO>> {
        return selectPage(mainTable = USER, query = query) { record ->
            record.into(UserDTO::class.java)
        }
    }
    fun DSLContext.selectUserById(userId: String): Mono<UserDTO> {
        return Mono.from(
            select(
                USER.ID,
                USER.NAME,
                USER.ROLE,
                USER.TYPE,
                USER.EMAIL,
                USER.PHONE_NUMBER,
                USER.KEY,
                USER.STATE,
                USER.BRANCH_SERIAL,
                USER.BRANCH_NAME,
                USER.CREATE_AT,
                jsonArrayAgg(
                    jsonObject(
                        key("id").value(SERVICE.ID),
                        key("name").value(SERVICE.NAME),
                        key("name_kr").value(SERVICE.NAME_KR)
                    )
                ).`as`("services")
            )
            .from(USER)
                .leftJoin(USER_SERVICE).on(USER.ID.eq(USER_SERVICE.USER_ID))
                .leftJoin(SERVICE).on(USER_SERVICE.SERVICE_ID.eq(SERVICE.ID))
            .where(USER.ID.eq(userId))
            .groupBy(USER.ID)
        ).map { it.into(UserDTO::class.java) }
    }

    fun DSLContext.updateUserById(user: UserDTO): Mono<User> {
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
        ).map{it.into(User::class.java)}
    }
}