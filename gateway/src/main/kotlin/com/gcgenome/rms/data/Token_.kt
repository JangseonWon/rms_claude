package com.gcgenome.rms.data

data class Token_(
    val nbf: Long,
    val exp: Long,
    val iss: String,
    val aud: String,
    val iat: Long,
    val jti: String,
    val id: String,
    val authorities: Array<String>,
    val name: String,
    val department: String? = null
)