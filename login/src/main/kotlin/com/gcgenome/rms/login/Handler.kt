package com.gcgenome.rms.login

import com.gcgenome.rms.authenticate.TokenFactory
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.exceptions.UserNotFoundException
import com.gcgenome.rms.tables.pojos.User
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class Handler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
    val token: TokenFactory
):UserDao {
    fun login(user: User): Mono<String>{
        return dslContext.dsl().selectUserById(user.id!!)
            .filter { encoder.matches(user.password, it.password) }
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .map(token::publish)
    }

    fun signup(user: User): Mono<User>{
        val dto = user.apply { password = encoder.encode(user.password) }
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run { insertUser(dto) }
        })
    }
}