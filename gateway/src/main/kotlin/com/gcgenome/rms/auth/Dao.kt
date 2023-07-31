package com.gcgenome.rms.auth

import com.gcgenome.rms.tables.records.UserRecord
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface Dao {
    fun DSLContext.selectUser(id: String): Mono<UserRecord>{
        return Mono.from(selectFrom(USER).where(USER.ID.eq(id)))
    }
    fun DSLContext.selectUserByKey(key: UUID): Mono<UserRecord>{
        return Mono.from(selectFrom(USER).where(USER.KEY.eq(key)))
    }
}