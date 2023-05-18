package com.gcgenome.rms.login

import com.gcgenome.rms.auth.TokenFactory
import com.gcgenome.rms.data.Login_
import com.gcgenome.rms.repo.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("com.gcgenome.rms.login.Handler")
class Handler(
    private val repo: UserRepository,
    private val token: TokenFactory
) {
    fun login(request: Login_): Mono<String> {
        return repo.findById(request.id)
            .filter{ request.password == it.password}
            .map (token::publish)
    }
}