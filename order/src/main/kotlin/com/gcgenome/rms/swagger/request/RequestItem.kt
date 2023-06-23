package com.gcgenome.rms.swagger.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "세부 의뢰들")
data class RequestItem(
    @Schema(description = "지놈 검사 코드")
    val service: String?,
    @Schema(description = "수진자")
    val patient: RequestPatient
)
