package com.gcgenome.rms.service

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.ManagerAuthenticationException
import com.gcgenome.rms.exception.PasswordNotMatchException
import com.gcgenome.rms.exception.UserNotFoundException
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserHandler(
    val dslContext: DSLContext,
    val encoder: BCryptPasswordEncoder
): UserDao, OrganizationDao {

    fun insertUser(authentication: UserAuthentication, userDto: User): Mono<User> {
        val password = encoder.encode(userDto.password!!)
        userDto.organization = Organization(userDto.id,userDto.id, userDto.name,null,null,null)
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                chkManager(authentication)
                    .flatMap { insertUser(userDto, password) }
                    .flatMap { insertOrganization(userDto.organization!!) }
                    .flatMap { selectUserOrganizationById(userDto) }
            }.map { it }
        })
    }

    fun updateUserById(authentication: UserAuthentication, userId: String, userDto: UpdateUser): Mono<User> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                chkManager(authentication)
                    .filterWhen{checkPassword(userDto)}.switchIfEmpty(Mono.error(PasswordNotMatchException()))
                    .then(selectUserById(userId).switchIfEmpty(Mono.error(UserNotFoundException(userId))))
                    .flatMap { updateUser(userId,userDto,trx) }
                    .flatMap { updateOrganizationNameByUserId(it)
                        .map { organization -> it.organization = organization; it } }
            }
        })
    }

    private fun checkPassword(userDto: UpdateUser):Mono<Boolean>{
        return userDto.password?.let { pw ->
            Mono.just(pw.password == pw.passwordConfirm)
        } ?: Mono.just(true)
    }

    fun updateUser(userId:String, userDto: UpdateUser, trx:Configuration): Mono<User>{
        var password:String? = null
        if (userDto.password!=null)
            password = encoder.encode(userDto.password.password)

        return Mono.from(trx.dsl().run {
            updateUserById(userId, password,userDto)
        })
    }

    fun chkManager(authentication: UserAuthentication): Mono<UserAuthentication> {
        return when(authentication.user.role) {
            "MANAGER","ADMIN" -> Mono.just(authentication)
            else -> Mono.error(ManagerAuthenticationException())
        }
    }

}