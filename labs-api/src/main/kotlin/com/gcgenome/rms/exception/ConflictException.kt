package com.gcgenome.rms.exception

import com.gcgenome.rms.data.FieldError
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ConflictException(
    val fieldErrors: List<FieldError> = emptyList(),
    val rejectValue: Any? = null,
    val code: ErrorCode = ErrorCode.CONFLICT
) : ResponseStatusException(HttpStatus.CONFLICT, "Conflict")
