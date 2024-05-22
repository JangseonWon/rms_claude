package com.gcgenome.rms.auth

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.data.Role
import com.gcgenome.rms.exception.AdminAuthenticationException
import com.gcgenome.rms.exception.ManagerAuthenticationException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ManagerAuthenticationHandler {
    fun chkManager(authentication: UserAuthentication): Mono<UserAuthentication> {
        return when(authentication.user.role) {
            Role.MANAGER.toString(),Role.ADMIN.toString() -> Mono.just(authentication)
            else -> Mono.error(ManagerAuthenticationException())
        }
    }

    fun chkAdmin(authentication: UserAuthentication): Mono<UserAuthentication> {
        return when(authentication.user.role) {
            Role.ADMIN.toString() -> Mono.just(authentication)
            else -> Mono.error(AdminAuthenticationException())
        }
    }

    fun chkMySelf(authentication: UserAuthentication, userId: String): Mono<UserAuthentication> {
        return if(authentication.user.id == userId) {
            Mono.just(authentication)
        } else {
            Mono.empty()
        }
    }

    fun chkUserAndManager(authentication: UserAuthentication, userId: String): Mono<UserAuthentication> {
        return when {
            authentication.user.role in listOf(Role.MANAGER.toString(), Role.ADMIN.toString()) -> Mono.just(authentication)
            authentication.user.id == userId -> Mono.just(authentication)
            else -> Mono.error(ManagerAuthenticationException())
        }
    }
}