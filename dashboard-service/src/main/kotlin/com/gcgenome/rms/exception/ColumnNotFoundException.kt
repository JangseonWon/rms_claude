package com.gcgenome.rms.exception

import org.jooq.exception.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.regex.Pattern

class ColumnNotFoundException(e: DataAccessException) {
    val errorMessage = e.message ?: ""
    val pattern = Pattern.compile("\"([^\"]+)\" 이름의 칼럼은 없습니다")
    val matcher = pattern.matcher(errorMessage)

    val message: String = if (matcher.find()) {
        val columnName = matcher.group(1)
        "해당 칼럼이 존재하지 않습니다: $columnName"
    } else {
        "해당 칼럼이 존재하지 않습니다."
    }

    fun toServerResponse(): Mono<ServerResponse> {
        return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(message)
    }
}