package com.gcgenome.rms.user

import com.gcgenome.rms.config.SecurityContextRepository.UserAuthentication
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.User
import com.gcgenome.rms.exception.ManagerAuthenticationException
import com.gcgenome.rms.exception.MatchUserException
import com.gcgenome.rms.exception.UserNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class Handler(
    val dslContext: DSLContext
): UserDao {
    fun selectUsers(): Flux<User> {
        return dslContext.dsl().selectUsers().map(User::toModel)
    }
    fun selectUserById(userId: String): Mono<User> {
        return dslContext.dsl()
                    .selectUserById(userId)
                    .switchIfEmpty(Mono.error(UserNotFoundException(userId)))
                    .map(User::toModel)
    }

    fun updateUserById(userId: String, userDto: User): Mono<User> {
        return Mono.from(dslContext.transactionPublisher { trx ->
                    trx.dsl().run {
                        selectUserById(userId)
                            .switchIfEmpty(Mono.error(UserNotFoundException(userId)))
                            .flatMap { updateUserById(userId, userDto) }
                            .map(User::toModel)
                    }
            })
    }

    fun chkManager(authentication: UserAuthentication): Mono<UserAuthentication>{
        return authentication.authorities
            .any { it.authority == "MANAGER" || it.authority == "ADMIN" }
            .takeIf { it }
            ?.let { Mono.just(authentication) }
            ?: Mono.error(ManagerAuthenticationException())
    }

    fun matchUser(authentication: UserAuthentication, userId: String): Mono<UserAuthentication>{
        return if(authentication.principal == userId
            || authentication.authorities.any { it.authority == "MANAGER" || it.authority == "ADMIN" }
            ) Mono.just(authentication)
        else Mono.error(MatchUserException())
    }
}