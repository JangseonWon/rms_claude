package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.USER
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono

interface UserDao : QueryDao{
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
}