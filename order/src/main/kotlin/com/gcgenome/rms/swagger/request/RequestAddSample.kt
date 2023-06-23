package com.gcgenome.rms.swagger.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "진행 중 의뢰에 검체 추가")
data class RequestAddSample(
    @Schema(description = "검체 종류 ID")
    val patient: RequestPatient
)