package com.gcgenome.rms.login

import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.query.LdapQueryBuilder
import org.springframework.stereotype.Service

@Service
class LdapLogin (private val ldapTemplate: LdapTemplate) {
    fun authenticate(userId: String, password: String): Boolean =
        runCatching {
            ldapTemplate.authenticate(LdapQueryBuilder.query().where("uid").`is`(userId), password)
        }.isSuccess
}