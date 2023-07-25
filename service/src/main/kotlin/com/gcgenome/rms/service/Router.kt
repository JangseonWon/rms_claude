package com.gcgenome.rms.service

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.Service
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.springdoc.core.fn.builders.parameter.Builder
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration("com.gcgenome.rms.service.Router")
class Router (private val handler: Handler) {
    @Bean("com.gcgenome.rms.service.Router.Bean")
    fun route() = router { GET("/api/services", ::services) }

    fun routes(): RouterFunction<ServerResponse> {
        val apiRoutes = SpringdocRouteBuilder.route()
            .GET("/api/services", ::services.toHandlerFunction()) {
                it.operationId("findServices")
                    .description("서비스 조회 API")
                    .parameter(
                        Builder.parameterBuilder().name("X-USER-ID").description("사용자 ID").required(true).`in`(
                            ParameterIn.HEADER))
                    .response(org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder().responseCode("200").content(
                        org.springdoc.core.fn.builders.content.Builder.contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                            .schema(org.springdoc.core.fn.builders.schema.Builder.schemaBuilder().implementation(Service::class.java))
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

    private fun services(request: ServerRequest): Mono<ServerResponse> {
        return request
            .principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(handler.list(it.principal), Service::class.java)
            }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume { e ->
                e.printStackTrace()
                ServerResponse.badRequest().bodyValue(e)
            }
    }
}

fun interface HandlerAdapter : (ServerRequest) -> Mono<ServerResponse>

fun ((ServerRequest) -> Mono<ServerResponse>).toHandlerFunction(): HandlerAdapter {
    return HandlerAdapter { request ->
        this.invoke(request)
    }
}