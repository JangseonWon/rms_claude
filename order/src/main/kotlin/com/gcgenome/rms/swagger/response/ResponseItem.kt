package com.gcgenome.rms.swagger.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "세부 의뢰들")
data class ResponseItem(
    @Schema(description = "지놈 의뢰항목 ID")
    var id: UUID?,
    @Schema(description = "지놈 검사 코드")
    val service: String?,
    @Schema(description = "의뢰일")
    val orderAt: LocalDateTime?,
    @Schema(description = "수진자")
    val patient: ResponsePatient
)
