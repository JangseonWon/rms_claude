package com.gcgenome.rms.order

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.exceptions.ServiceNotFoundException
import com.gcgenome.rms.exceptions.ServiceSampleTypeNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@Configuration
class OrderRouter (
    private val handler: OrderHandler
){
    @Bean("OrderServiceRouter")
    fun route() = router {
        PUT("/w-api/product-service/orders", ::orders)
    }

    private fun orders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(object : ParameterizedTypeReference<List<Item>>() {}))
            .flatMap { handler.insertOrderRequest(it.t1.principal, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume (ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume (ServiceSampleTypeNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
            .onErrorResume(ServerWebInputException::class.java) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body type error.") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body error.") }
    }
}
