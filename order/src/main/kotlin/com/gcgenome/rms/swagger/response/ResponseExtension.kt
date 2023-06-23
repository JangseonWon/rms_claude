package com.gcgenome.rms.swagger.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "추가 등록 정보")
data class ResponseExtension(
    @Schema(description = "추가 등록 정보 ID")
    val id: String?,
    @Schema(description = "추가 등록 정보 값")
    val value: String?
)
