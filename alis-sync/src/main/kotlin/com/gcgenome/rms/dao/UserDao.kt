package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.tables.records.UserRecord
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface UserDao{
    fun DSLContext.insertUser(rmsOrder: RmsOrder): Mono<UserRecord> {
        return Mono.from(
            insertInto(USER)
                .set(USER.ID, rmsOrder.organizationUserId)
                .set(USER.AUTHORITY, "USER")
                .set(USER.NAME, rmsOrder.organizationName)
                .set(USER.STATE, "ACTIVATE")
                .set(USER.CODE, rmsOrder.userCode)
                .onDuplicateKeyUpdate()
                .set(USER.AUTHORITY, "USER")
                .returning()
        )
    }
}
