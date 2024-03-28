package com.gcgenome.rms.authentication

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="spring.security.oauth2.authentication")
class AuthenticationConfig {
    lateinit var header: String
    lateinit var jwt: JwtConfig
}
class JwtConfig {
    lateinit var secret: String
}