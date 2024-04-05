package com.gcgenome.rms.dao


import com.gcgenome.rms.data.UserService
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface UserServiceDao {
    fun DSLContext.insertUserService(userId:String, serviceId: String): Mono<UserService> {
        return Mono.from(insertInto(USER_SERVICE)
            .set(USER_SERVICE.USER_ID, userId)
            .set(USER_SERVICE.SERVICE_ID, serviceId)
            .set(USER_SERVICE.CREATE_AT, LocalDateTime.now())
            .onDuplicateKeyIgnore()
            .returning()
        ).map { it.into(UserService::class.java) }
    }
}