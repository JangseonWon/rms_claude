package com.gcgenome.rms.exception

import com.gcgenome.rms.data.FieldError
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NotFoundException(
    val fieldErrors: List<FieldError> = emptyList(),
    val code: ErrorCode = ErrorCode.NOT_FOUND
) : ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")