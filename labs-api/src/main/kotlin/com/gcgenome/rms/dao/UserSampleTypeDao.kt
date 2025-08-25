package com.gcgenome.rms.dao

import com.gcgenome.rms.data.UserSampleTypeDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.UUID

interface UserSampleTypeDao {
    fun DSLContext.selectUserSampleTypeByUserIdAndSerial(userId: UUID, serial: String): Mono<UserSampleTypeDTO> {
        return Mono.from(
            selectFrom(USER_SAMPLE_TYPE).where(
                USER_SAMPLE_TYPE.USER_ID.eq(userId),
                USER_SAMPLE_TYPE.SERIAL.eq(serial)
            )
        ).map { it.into(UserSampleTypeDTO::class.java) }
    }
}