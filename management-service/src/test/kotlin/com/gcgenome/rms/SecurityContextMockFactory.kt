package com.gcgenome.rms

import com.gcgenome.rms.authentication.User
import com.gcgenome.rms.authentication.UserAuthentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.time.LocalDateTime

@WithSecurityContext(factory = SecurityContextMockFactory::class)
annotation class WithMockUser(
    val id: String = "USER_ID",
    val name: String = "USER_NAME",
    val role: String = ""
)

class SecurityContextMockFactory: WithSecurityContextFactory<WithMockUser> {
    override fun createSecurityContext(user: WithMockUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()
        val auth = UserAuthentication(
            id= user.id,
            user = User(
                user.id, user.name,
                null, "", null,
                user.role.ifBlank { null },
                null,null,null),
            issuer = "",
            audience = emptySet(),
            issuedDateTime = LocalDateTime.now(),
            notBeforeDateTime = LocalDateTime.now().minusSeconds(1),
            expireDateTime = LocalDateTime.now().plusHours(1),
            token = ""
        )
        context.authentication = auth
        return context
    }
}