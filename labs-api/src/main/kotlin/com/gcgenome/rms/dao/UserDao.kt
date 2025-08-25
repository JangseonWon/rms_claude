package com.gcgenome.rms.dao

import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface UserDao {
    fun DSLContext.selectUserByKey(key: UUID): Mono<UserDTO> {
        return Mono.from(
            selectFrom(USER).where(USER.KEY.eq(key))
        ).map { it.into(UserDTO::class.java) }
    }
}