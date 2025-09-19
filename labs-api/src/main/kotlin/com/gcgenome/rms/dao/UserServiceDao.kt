package com.gcgenome.rms.dao

import com.gcgenome.rms.data.UserServiceDTO
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface UserServiceDao {
    fun DSLContext.selectUserServiceByUserIdAndSerial(userId: UUID, serial: String): Mono<UserServiceDTO> {
        return Mono.from(
            selectFrom(USER_SERVICE).where(
                USER_SERVICE.USER_ID.eq(userId),
                USER_SERVICE.SERIAL.eq(serial)
            )
        ).map { it.into(UserServiceDTO::class.java) }
    }
}