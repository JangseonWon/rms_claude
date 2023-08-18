package com.gcgenome.rms.order

import com.gcgenome.rms.SecurityContextRepository
import com.gcgenome.rms.data.CancelOrder
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Order
import com.gcgenome.rms.exceptions.SampleNotFoundException
import com.gcgenome.rms.exceptions.ServiceNotFoundException
import com.gcgenome.rms.exceptions.ServiceSampleTypeNotFoundException
import com.gcgenome.rms.swagger.request.RequestAddSample
import com.gcgenome.rms.swagger.request.RequestOrder
import com.gcgenome.rms.swagger.response.ResponseCancelOrder
import com.gcgenome.rms.swagger.response.ResponseOrder
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.springdoc.core.fn.builders.parameter.Builder
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RequestPredicates.contentType

import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class Router (
    private val handler: Handler
){
    @Bean
    /*fun route() = router {
        PUT("/api/orders", ::orders)
        GET("/api/orders", ::findOrders)
        GET("/api/orders/samples/{sampleId}, findOrder)
        PATCH("/api/orders/items/{item-id}", ::updateOrders)
        DELETE("/api/samples/{sample-id}", ::cancels)
        PATCH("/api/orders/samples/{sampleId}", :: addSample)
    }*/
    fun routes(): RouterFunction<ServerResponse> {
        val apiRoutes = SpringdocRouteBuilder.route()
            .GET("/api/orders", contentType(MediaType("application", "vnd.request.v1", Charsets.UTF_8)), ::findOrders.toHandlerFunction()) {
                it.operationId("findOrders")
                    .description("의뢰 조회 API")
                    .parameter(Builder.parameterBuilder().name("X-USER-ID").description("사용자 ID").required(true).`in`(ParameterIn.HEADER))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("200").content(
                        org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(ResponseOrder::class.java))
                    ))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("400").description("잘못된 접근: 필수 파라메터 누락, 타입 불일치, 잘못된 포맷 등"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("401").description("인증실패: 인증정보 누락"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("403").description("인증실패: 인증정보 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("404").description("잘못된 접근: 잘못된 URL"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("405").description("잘못된 접근: 요청된 URL과 Method 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("406").description("요청 처리 불가: 요청 처리 불가"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("500").description("예기치 못한 원인: 서버 내부 에러"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("503").description("서비스 제공 불가: 서버가 동작하지 않음"))
            }.GET("/api/orders/samples/{sampleId}", ::findOrder.toHandlerFunction()) {
                it.operationId("findOrder")
                    .description("의뢰 조회 API")
                    .parameter(Builder.parameterBuilder().name("X-USER-ID").description("사용자 ID").required(true).`in`(ParameterIn.HEADER))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("200").content(
                        org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(ResponseOrder::class.java))
                    ))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("400").description("잘못된 접근: 필수 파라메터 누락, 타입 불일치, 잘못된 포맷 등"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("401").description("인증실패: 인증정보 누락"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("403").description("인증실패: 인증정보 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("404").description("잘못된 접근: 잘못된 URL"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("405").description("잘못된 접근: 요청된 URL과 Method 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("406").description("요청 처리 불가: 요청 처리 불가"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("500").description("예기치 못한 원인: 서버 내부 에러"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("503").description("서비스 제공 불가: 서버가 동작하지 않음"))
            }
            .POST("/api/orders", contentType(MediaType("application", "vnd.request.v1+json", Charsets.UTF_8)), ::orders.toHandlerFunction()) {
                it.operationId("orders")
                    .description("의뢰 등록 API")
                    .parameter(Builder.parameterBuilder().name("X-USER-ID").description("사용자 ID").required(true).`in`(ParameterIn.HEADER))
                    .requestBody(
                        org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder()
                            .content(
                                org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                    .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(
                                            RequestOrder::class.java).description("의뢰 내용")))
                    )
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("200").content(
                        org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(ResponseOrder::class.java))
                    ))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("400").description("잘못된 접근: 필수 파라메터 누락, 타입 불일치, 잘못된 포맷 등"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("401").description("인증실패: 인증정보 누락"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("403").description("인증실패: 인증정보 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("404").description("잘못된 접근: 잘못된 URL"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("405").description("잘못된 접근: 요청된 URL과 Method 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("406").description("요청 처리 불가: 요청 처리 불가"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("500").description("예기치 못한 원인: 서버 내부 에러"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("503").description("서비스 제공 불가: 서버가 동작하지 않음"))
            }
            .PATCH("/api/orders/items/{item-id}", ::updateOrders.toHandlerFunction()) {
                it.operationId("updateOrders")
                    .description("의뢰 품목 수정 API")
                    .parameter(Builder.parameterBuilder().name("X-USER-ID").description("사용자 ID").required(true).`in`(ParameterIn.HEADER))
                    .requestBody(
                        org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder()
                            .content(org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                    .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(ResponseOrder::class.java)
                                        .description("의뢰 내용"))))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("200").content(
                        org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(ResponseOrder::class.java))
                    ))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("400").description("잘못된 접근: 필수 파라메터 누락, 타입 불일치, 잘못된 포맷 등"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("401").description("인증실패: 인증정보 누락"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("403").description("인증실패: 인증정보 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("404").description("잘못된 접근: 잘못된 URL"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("405").description("잘못된 접근: 요청된 URL과 Method 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("406").description("요청 처리 불가: 요청 처리 불가"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("500").description("예기치 못한 원인: 서버 내부 에러"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("503").description("서비스 제공 불가: 서버가 동작하지 않음"))
            }
            .PATCH("/api/orders/samples/{sampleId}", :: addSample.toHandlerFunction()) {
                it.operationId("ReSamples")
                    .description("ReSample API")
                    .parameter(Builder.parameterBuilder().name("X-USER-ID").description("사용자 ID").required(true).`in`(ParameterIn.HEADER))
                    .parameter(Builder.parameterBuilder().name("sample-id").description("Sample Id").required(true).`in`(ParameterIn.PATH))
                    .requestBody(org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder()
                        .content(org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(
                                RequestAddSample::class.java).description("의뢰 내용"))))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("200").content(
                        org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(ResponseOrder::class.java))
                    ))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("400").description("잘못된 접근: 필수 파라메터 누락, 타입 불일치, 잘못된 포맷 등"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("401").description("인증실패: 인증정보 누락"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("403").description("인증실패: 인증정보 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("404").description("잘못된 접근: 잘못된 URL"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("405").description("잘못된 접근: 요청된 URL과 Method 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("406").description("요청 처리 불가: 요청 처리 불가"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("500").description("예기치 못한 원인: 서버 내부 에러"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("503").description("서비스 제공 불가: 서버가 동작하지 않음"))
            }
            .DELETE("/api/orders/samples/{sample-id}", contentType(MediaType("application", "vnd.request.v1", Charsets.UTF_8)), ::cancels.toHandlerFunction()) {
                it.operationId("deleteOrders")
                    .description("의뢰 삭제 API")
                    .parameter(Builder.parameterBuilder().name("X-USER-ID").description("사용자 ID").required(true).`in`(ParameterIn.HEADER))
                    .parameter(Builder.parameterBuilder().name("sample-id").description("Sample Id").required(true).`in`(ParameterIn.PATH))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("200").content(
                        org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(ResponseCancelOrder::class.java))
                    ))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("400").description("잘못된 접근: 필수 파라메터 누락, 타입 불일치, 잘못된 포맷 등"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("401").description("인증실패: 인증정보 누락"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("403").description("인증실패: 인증정보 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("404").description("잘못된 접근: 잘못된 URL"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("405").description("잘못된 접근: 요청된 URL과 Method 불일치"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("406").description("요청 처리 불가: 요청 처리 불가"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("500").description("예기치 못한 원인: 서버 내부 에러"))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("503").description("서비스 제공 불가: 서버가 동작하지 않음"))
            }
            .build()

        return apiRoutes
    }

    private fun orders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Order::class.java))
            .flatMap { handler.insertOrder(it.t1.principal, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
            .onErrorResume (ServiceNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume (ServiceSampleTypeNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
            .onErrorResume(ServerWebInputException::class.java) { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body type error.") }
            .onErrorResume { e -> ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Request body error: ${e.message}") }
    }


    private fun findOrders(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(handler.findOrders(it.principal), Order::class.java)
            }
    }
    private fun findOrder(request: ServerRequest): Mono<ServerResponse> {
        val sampleId = UUID.fromString(request.pathVariable("sampleId"))
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.findOrder(sampleId, it.principal) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it),Order::class.java) }
            .onErrorResume (SampleNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Reason phrase: ${e.message}") }
    }
    private fun addSample(request: ServerRequest): Mono<ServerResponse> {
        val sampleId = UUID.fromString(request.pathVariable("sampleId"))
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Item::class.java))
            .flatMap { handler.addSample(it.t1.principal, sampleId, it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
    }


    private fun updateOrders(request: ServerRequest): Mono<ServerResponse> {
        val itemIdString = request.pathVariable("item-id")
        val itemId = UUID.fromString(itemIdString)
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Item::class.java))
            .flatMap { handler.updateOrder(it.t1.principal, itemId ,it.t2) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(it), Order::class.java) }
    }


    private fun cancels(request: ServerRequest): Mono<ServerResponse> {
        val sampleIdString = request.pathVariable("sample-id")
        val sampleId = UUID.fromString(sampleIdString)
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap { handler.cancelOrder(sampleId) }
            .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(it), CancelOrder::class.java) }
    }
}

fun interface HandlerAdapter : (ServerRequest) -> Mono<ServerResponse>

fun ((ServerRequest) -> Mono<ServerResponse>).toHandlerFunction(): HandlerAdapter {
    return HandlerAdapter { request ->
        this.invoke(request)
    }
}