package com.gcgenome.rms.exception

import com.gcgenome.rms.data.FieldError
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UnprocessableEntityException(
    val fieldErrors: List<FieldError> = emptyList(),
    val code: ErrorCode = ErrorCode.VALIDATION_FAILED
) : ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed")

