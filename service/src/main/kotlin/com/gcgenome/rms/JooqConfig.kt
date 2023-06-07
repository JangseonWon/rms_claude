package com.gcgenome.rms

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.reactivestreams.Publisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux

@Configuration
class JooqConfig {
    @Bean
    fun jooqDsl(connectionFactory: ConnectionFactory): DSLContext {
        return DSL.using(connectionFactory)
    }
}
fun <T> Publisher<T>.toFlux() = Flux.from(this)