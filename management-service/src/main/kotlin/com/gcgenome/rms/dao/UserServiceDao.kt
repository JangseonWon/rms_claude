package com.gcgenome.rms.dao


import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface UserServiceDao: QueryDao {
    fun DSLContext.insertUserService(userService: UserServiceDTO): Mono<UserServiceDTO> {
        return Mono.from(insertInto(USER_SERVICE)
            .set(USER_SERVICE.USER_ID, userService.userId)
            .set(USER_SERVICE.SERVICE_ID, userService.serviceId)
            .set(USER_SERVICE.CREATE_AT, LocalDateTime.now())
            .onDuplicateKeyIgnore()
            .returning()
        ).map { it.into(UserServiceDTO::class.java) }
    }

    fun DSLContext.selectUserServiceById(userService: UserServiceDTO): Mono<UserServiceDTO> {
        return Mono.from(
            selectFrom(USER_SERVICE)
                .where(USER_SERVICE.USER_ID.eq(userService.userId).and(USER_SERVICE.SERVICE_ID.eq(userService.serviceId)))
        ).map { it.into(UserServiceDTO::class.java) }
    }

    fun DSLContext.deleteUserService(userService: UserServiceDTO): Mono<UserServiceDTO> {
        return Mono.from(
            deleteFrom(USER_SERVICE)
                .where(USER_SERVICE.USER_ID.eq(userService.userId).and(USER_SERVICE.SERVICE_ID.eq(userService.serviceId)))
                .returning()
        ).map { it.into(UserServiceDTO::class.java) }
    }
}