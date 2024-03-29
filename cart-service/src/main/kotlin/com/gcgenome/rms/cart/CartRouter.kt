package com.gcgenome.rms.cart

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.exceptions.OrderNotFoundException
import com.gcgenome.rms.exceptions.UserNotFoundException
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
        GET("/w-api/cart-service/orders/{order_id}/samples/{sample_id}/services/{service_id}", ::cartInfo)
    }

    private fun cartInfo(request: ServerRequest): Mono<ServerResponse> {
        val orderId = UUID.fromString(request.pathVariable("order_id"))
        val sampleId = UUID.fromString(request.pathVariable("sample_id"))
        val serviceId = request.pathVariable("service_id")
        return request.principal()
            .cast(UserAuthentication::class.java)
            .switchIfEmpty(Mono.error(UserNotFoundException()))
            .flatMap { handler.getCartInfo(orderId, sampleId, serviceId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume (UserNotFoundException::class.java){ ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("User not authenticated") }
            .onErrorResume(OrderNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
    }
}
