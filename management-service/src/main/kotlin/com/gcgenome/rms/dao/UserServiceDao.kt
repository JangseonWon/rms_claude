package com.gcgenome.rms.dao


import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface UserServiceDao: QueryDao {
    fun DSLContext.insertUserService(userId: String, serviceId: String): Mono<UserServiceDTO> {
        return Mono.from(
            insertInto(USER_SERVICE)
                .set(USER_SERVICE.USER_ID, userId)
                .set(USER_SERVICE.SERVICE_ID, serviceId)
                .set(USER_SERVICE.CREATE_AT, LocalDateTime.now())
                .returning()
        ).map { it.into(UserServiceDTO::class.java) }
    }

    fun DSLContext.deleteUserServiceByUserId(userId: String): Mono<UserServiceDTO> {
        return Mono.from(
            deleteFrom(USER_SERVICE)
                .where(USER_SERVICE.USER_ID.eq(userId))
                .returning()
        ).map { it.into(UserServiceDTO::class.java) }
    }
}