package com.gcgenome.rms.request.dto.response

import java.util.*

data class RequestResponseDTO(
    val id: UUID,
    val department: String? = null
)

data class ExtensionResponseDTO(
    val id: UUID,
    val code: String,
    val nameKr: String? = null,
    val nameEn: String? = null,
    val regex: String? = null
)
