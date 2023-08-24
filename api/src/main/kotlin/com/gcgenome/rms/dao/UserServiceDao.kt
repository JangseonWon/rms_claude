package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.UserServiceRecord
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface UserServiceDao {
    fun DSLContext.selectUserServiceById(userId: String, serviceId: String): Mono<UserServiceRecord> {
        return Mono.from(selectFrom(USER_SERVICE).where(USER_SERVICE.USER_ID.eq(userId).and(USER_SERVICE.SERVICE_ID.eq(serviceId))))
    }
}