package com.gcgenome.rms.login

import com.gcgenome.rms.authenticate.TokenFactory
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.User
import com.gcgenome.rms.exceptions.UserNotFoundException
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class Handler(
    val ldapLogin: LdapLogin,
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
    val token: TokenFactory
):UserDao {
    fun login(user: User): Mono<String>{
        return loginByLdap(user)
            .switchIfEmpty(loginByRms(user))
            .map(token::publish)
    }
    fun loginByLdap(user: User): Mono<User> {
        return if (ldapLogin.authenticate(user.id, user.password!!)){
            Mono.from(dslContext.transactionPublisher { trx ->
                trx.dsl().run {
                    selectUserById(user.id).switchIfEmpty(insertUser(user)).map(User::toModel)
                }
            })
        } else Mono.empty()
    }

    fun loginByRms(user: User): Mono<User>{
        return dslContext.dsl().selectUserById(user.id)
            .filter { encoder.matches(user.password, it.password) }
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map(User::toModel)
    }
}