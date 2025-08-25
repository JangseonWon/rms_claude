package com.gcgenome.rms.data

import com.gcgenome.rms.exception.ErrorCode
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: String = LocalDateTime.now().toString(),
    val status: Int,
    val path: String,
    val error: String,
    val code: ErrorCode? = null,
    val fieldErrors: List<FieldError>? = null,
    val traceId: String? = null
)

data class FieldError(
    val field: String,
    val message: String,
    val rejectedValue: Any? = null
)