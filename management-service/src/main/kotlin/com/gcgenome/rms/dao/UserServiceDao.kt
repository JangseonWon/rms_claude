package com.gcgenome.rms.dao


import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface UserServiceDao: QueryDao {
    fun DSLContext.selectUserServices(userId: String): Flux<UserServiceDTO> {
        return Flux.from(
            selectFrom(USER_SERVICE)
                .where(USER_SERVICE.USER_ID.eq(userId))
        ).map { it.into(UserServiceDTO::class.java) }
    }

    fun DSLContext.insertUserService(userId: String, serviceId: String): Mono<UserServiceDTO> {
        return Mono.from(
            insertInto(USER_SERVICE)
                .set(USER_SERVICE.USER_ID, userId)
                .set(USER_SERVICE.SERVICE_ID, serviceId)
                .set(USER_SERVICE.CREATE_AT, LocalDateTime.now())
                .returning()
        ).map { it.into(UserServiceDTO::class.java) }
    }
    fun DSLContext.deleteUserServiceByUserIdAndServiceId(userId: String, serviceId: String): Mono<UserServiceDTO> {
        return Mono.from(
            deleteFrom(USER_SERVICE)
                .where(
                    USER_SERVICE.USER_ID.eq(userId),
                    USER_SERVICE.SERVICE_ID.eq(serviceId)
                )
                .returning()
        ).map { it.into(UserServiceDTO::class.java) }
    }

}