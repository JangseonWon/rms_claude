package com.gcgenome.rms.cart

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.exceptions.ItemNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class CartRouter (
    private val handler: CartHandler
){
    @Bean("CartServiceRouter")
    fun route() = router {
        GET("/w-api/cart-service/orders/{order_id}/samples/{sample_id}", ::cartInfo)
    }

    private fun cartInfo(request: ServerRequest): Mono<ServerResponse> {
        val orderId = UUID.fromString(request.pathVariable("order_id"))
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.getCartInfo(orderId, sampleId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume(ItemNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
}
