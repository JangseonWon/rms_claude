package com.gcgenome.rms.service

import com.gcgenome.lims.encrypt.SHA256
import com.gcgenome.rms.config.SecurityContextRepository.UserAuthentication
import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.*
import com.gcgenome.rms.exception.ManagerAuthenticationException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserHandler(
    val dslContext: DSLContext
): UserDao, OrganizationDao {

    fun insertUser(authentication: UserAuthentication, userDto: User): Mono<User> {
        val password = SHA256.convert(userDto.password!!)
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

    fun chkManager(authentication: UserAuthentication): Mono<UserAuthentication>{
        return authentication.authorities
            .any { it.authority == "MANAGER" || it.authority == "ADMIN" }
            .takeIf { it }
            ?.let { Mono.just(authentication) }
            ?: Mono.error(ManagerAuthenticationException())
    }

}