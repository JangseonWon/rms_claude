package com.gcgenome.rms.profile.user

import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.UserDTO
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder,
): UserDao {

    fun selectUserById(userId: String): Mono<UserDTO> {
        return dslContext.selectUserById(userId)
    }

    fun updateUserById(user: UserDTO): Mono<UserDTO> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                user.password = user.password?.takeIf { it.isNotBlank() }?.let { encoder.encode(it) }
                updateUserById(user)
            }
        })
    }
}