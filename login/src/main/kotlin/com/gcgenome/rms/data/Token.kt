package com.gcgenome.rms.data

data class Token(
    val nbf : Long,				    // 토큰 유효 시각 milliseconds
    val exp : Long,				    // 토큰 만료 시각 milliseconds	nbf < current time < exp
    val iss : String,				// 토큰 발급 서비스, rms-test.gcgenome.com
    val aud : String,				// 토큰 사용 서비스, rms-test.gcgenome.com
    val iat : Long,				    // 토큰 발급 시각
    val jti : String,				// 토큰 식별자
    val id : String,
    val authorities : Array<String>,
    val name : String?,
    val department : String?,
)
