package com.gcgenome.rms.service

import com.gcgenome.rms.SecurityContextRepository
import com.gcgenome.rms.data.Service_
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import java.security.Principal

@Service("com.gcgenome.rms.service.Handler")
class Handler(private val dao: Dao) {
    fun list(userId: String): Flux<Service_> {
        return dao.findByUserService(userId)
    }
}