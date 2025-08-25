package com.gcgenome.rms.dao

import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.tables.references.USER
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
class UserDao_(
    private val dsl: DSLContext
) {
    fun selectUserByKey(key: UUID): Mono<UserDTO> {
        return Mono.from(
            dsl.selectFrom(USER).where(USER.KEY.eq(key))
        ).map { it.into(UserDTO::class.java) }
    }
}