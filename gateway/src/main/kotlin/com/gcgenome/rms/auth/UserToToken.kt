package com.gcgenome.rms.auth

import com.gcgenome.rms.data.Token_
import com.gcgenome.rms.entity.User
import java.util.*

object UserToToken {
    fun map(nbf: Long, exp: Long, iss: String, aud: String, iat: Long, user: User): Token_ {
        return Token_(nbf, exp, iss, aud, iat, UUID.randomUUID().toString(), user.id, arrayOf("ROLE_USER"), user.name, user.department)
    }
}