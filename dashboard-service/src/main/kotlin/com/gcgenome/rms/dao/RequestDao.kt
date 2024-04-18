package com.gcgenome.rms.dao

import com.gcgenome.rms.data.StatusCount
import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.tables.references.ORDER
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.*
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono

interface RequestDao {
    fun DSLContext.selectStatusCount(user: User): Mono<StatusCount> {
        return Mono.from(
            select(
                count(),
                count().filterWhere("status = 'ORDERED'"),
                count().filterWhere("status = 'SPECIFIED'"),
                count().filterWhere("status = 'INPROGRESS'"),
                count().filterWhere("status = 'TESTFAILED'"),
                count().filterWhere("status = 'DELIVERED'"),
                count().filterWhere("status = 'FINISHED'"),
                count().filterWhere("status = 'CART'")
            )
                .from(REQUEST)
                .join(ORDER).on(REQUEST.ORDER_ID.eq(ORDER.ID))
                .where().apply {
                    when {
                        user.role.equals("USER") -> and(ORDER.USER_ID.eq(user.id))
                    }
                }
        ).map(StatusCount::toModel)
    }
}