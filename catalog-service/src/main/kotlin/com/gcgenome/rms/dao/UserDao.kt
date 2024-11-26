package com.gcgenome.rms.dao

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.pojos.Service
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import org.jooq.impl.DSL.noCondition
import reactor.core.publisher.Flux

interface UserDao : QueryDao{
    fun DSLContext.selectUserWithServicesQuery(user: UserAuthentication, query: Query): Flux<Service> {
        val joins = mutableListOf<QueryDao.JoinInfo>()
        var where = noCondition()

        if (user.user.role == "USER") {
            joins.add(QueryDao.JoinInfo(USER_SERVICE, USER_SERVICE.SERVICE_ID.eq(SERVICE.ID), QueryDao.JoinType.LEFT))
            where = USER_SERVICE.USER_ID.eq(user.user.id!!)
        }

        val fields = listOf(
            SERVICE.ID,
            SERVICE.NAME
        )

        val groupByFields = listOf(SERVICE.ID)

        return selectQueryFlux(
            mainTable = SERVICE,
            query = query,
            selectFields = fields,
            joinTables = joins,
            where = where,
            groupByFields = groupByFields,
        ) { record ->
            record.into(Service::class.java)
        }
    }
}