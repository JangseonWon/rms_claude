package com.gcgenome.rms.config

import org.springframework.security.core.GrantedAuthority

class UserGrantedAuthority(
    private val authority: String
): GrantedAuthority {
    override fun getAuthority(): String {
        return authority
    }
}