package com.gcgenome.rms.report

import com.gcgenome.rms.config.SecurityContextRepository
import com.gcgenome.rms.data.Report
import com.gcgenome.rms.exceptions.CompletedReportException
import com.gcgenome.rms.exceptions.MaxAllowedDaysException
import com.gcgenome.rms.exceptions.NoSuchParamException
import com.gcgenome.rms.exceptions.ReportNotFoundException
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.*


@Component
class Router(
    val handler: Handler
) {
    @Bean
    fun route() = router {
        GET("/api/reports", contentType(MediaType("application", "vnd.api.v1", Charsets.UTF_8)), ::findReports)
        GET("/api/reports/{reportId}", contentType(MediaType("application", "vnd.api.v1", Charsets.UTF_8)), ::reportDownload)
        POST("/api/reports/samples/{sampleId}", contentType(MediaType("application", "vnd.api.v1+json")), ::reportUpload)
    }
    fun findReports(request: ServerRequest): Mono<ServerResponse> {
        return try{
            val orderDateFrom = LocalDate.parse(request.queryParam("order-date-from").orElseThrow { NoSuchParamException("의뢰시작일")}).atStartOfDay()
            val orderDateTo = LocalDate.parse(request.queryParam("order-date-to").orElseThrow { NoSuchParamException("의뢰종료일")}).atTime(LocalTime.MAX)
            if (ChronoUnit.DAYS.between(orderDateFrom, orderDateTo) > 6) throw MaxAllowedDaysException()
            request.principal()
                .cast(SecurityContextRepository.UserAuthentication::class.java)
                .flatMap { handler.findReports(it.principal, orderDateFrom, orderDateTo).collectList() }
                .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(it) }
                .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Failed to search: ${e.message}") }
        }catch (e: NoSuchParamException){ ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}") }
        catch (e: MaxAllowedDaysException) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message}")}
        catch (e: DateTimeParseException) { ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("${e.message} 날짜 형식이 맞지 않습니다. YYYY-MM-DD 형식으로 작성 부탁드립니다.")}
    }
    fun reportDownload(request: ServerRequest): Mono<ServerResponse> {
        return try {
            val reportId: UUID = UUID.fromString(request.pathVariable("reportId"))
            request.principal()
                .cast(SecurityContextRepository.UserAuthentication::class.java)
                .flatMap { handler.downloadReport(it.principal, reportId) }
                .flatMap { ServerResponse.ok().contentType(MediaType.APPLICATION_PDF).bodyValue(it.asByteArray()) }
                .onErrorResume(CompletedReportException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("${e.message}")}
                .onErrorResume(ReportNotFoundException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("${e.message}") }
                .onErrorResume { e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Failed to download: ${e.message}") }
        } catch (e: NumberFormatException) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Invalid reportId: ${request.pathVariable("reportId")}")
        }
    }

    fun reportUpload(request: ServerRequest): Mono<ServerResponse> {
        val sampleId: UUID = UUID.fromString(request.pathVariable("sampleId"))
        return request.principal()
            .cast(SecurityContextRepository.UserAuthentication::class.java)
            .zipWith(request.bodyToMono(Report::class.java))
            .flatMap { handler.saveReport(sampleId, it.t2) }
            .flatMap { ServerResponse.ok().build() }
    }
}