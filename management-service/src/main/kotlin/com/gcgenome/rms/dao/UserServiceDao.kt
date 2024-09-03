package com.gcgenome.rms.dao


import com.gcgenome.rms.data.User
import com.gcgenome.rms.data.UserService
import com.gcgenome.rms.tables.pojos.Service
import com.gcgenome.rms.tables.references.SERVICE
import com.gcgenome.rms.tables.references.USER_SERVICE
import org.jooq.Condition
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

    fun DSLContext.selectUserByServiceIdOrName(userId: String, where: Condition): Flux<Service> {
        return Flux.from(
            select(SERVICE.ID, SERVICE.NAME).from(USER_SERVICE)
                .join(SERVICE).on(USER_SERVICE.SERVICE_ID.eq(SERVICE.ID))
                .where(USER_SERVICE.USER_ID.eq(userId).and(where))
        ).map { it.into(Service::class.java) }
    }

    fun DSLContext.getUserService(): Flux<UserService> {
        return Flux.from(
            selectFrom(USER_SERVICE)
        ).map { it.into(UserService::class.java) }
    }

    fun DSLContext.selectUserByServiceId(userId: String, serviceId: String): Mono<Service> {
        return Mono.from(
            selectFrom(USER_SERVICE).where(USER_SERVICE.USER_ID.eq(userId).and(USER_SERVICE.SERVICE_ID.eq(serviceId)))
        ).map { it.into(Service::class.java) }
    }

    fun DSLContext.deleteUserService(userId: String, serviceId: String): Mono<UserService> {
        return Mono.from(
            deleteFrom(USER_SERVICE)
                .where(USER_SERVICE.USER_ID.eq(userId).and(USER_SERVICE.SERVICE_ID.eq(serviceId)))
                .returning()
        ).map { it.into(UserService::class.java) }
    }

    fun DSLContext.insertServiceByServiceId(users: List<User>, serviceId: String): Flux<UserService> {
        return Flux.fromIterable(users)
            .flatMap { user ->
                Mono.from(
                    insertInto(USER_SERVICE)
                        .set(USER_SERVICE.USER_ID, user.id)
                        .set(USER_SERVICE.SERVICE_ID, serviceId)
                        .set(USER_SERVICE.CREATE_AT, LocalDateTime.now())
                        .returning()
                ).map { it.into(UserService::class.java) }
            }
    }

    fun DSLContext.deleteServiceByServiceId(serviceId: String): Mono<UserService> {
        return Mono.from(
            deleteFrom(USER_SERVICE)
                .where(USER_SERVICE.SERVICE_ID.eq(serviceId))
                .returning()
        ).map { it.into(UserService::class.java) }
    }
}