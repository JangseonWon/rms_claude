package com.gcgenome.rms.config

import com.gcgenome.rms.data.UserDTO
import org.springframework.security.authentication.AbstractAuthenticationToken


class CustomAuthenticationToken(
    val user: UserDTO
) : AbstractAuthenticationToken(emptyList()) {

    override fun getCredentials() = null
    override fun getPrincipal(): Any = user

    init {
        isAuthenticated = true
    }
}