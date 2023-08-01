package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.USER
import com.gcgenome.rms.tables.records.UserRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.dao.UserDao")
interface UserDao{
    fun DSLContext.selectUserById(userId: String): Mono<UserRecord> {
        return Mono.from(selectFrom(USER).where(USER.ID.eq(userId)))
    }
}
