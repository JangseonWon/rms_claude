package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.UserServiceRecord
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface UserServiceDao {

    fun DSLContext.insertUserService(serviceId: String, userId: String): Mono<UserServiceRecord> =
        Mono.from(
            insertInto(USER_SERVICE)
                .columns(USER_SERVICE.USER_ID, USER_SERVICE.SERVICE_ID)
                .values(userId, serviceId)
                .returning()
        )
}