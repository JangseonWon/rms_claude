package com.gcgenome.rms.swagger.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "하나의 의뢰")
data class RequestOrder(
    @Schema(description = "시검여부")
    val test: Boolean?,
    @Schema(description = "외상여부")
    val credit: Boolean?,
    @Schema(description = "검사금액")
    val price: Int?,
    @Schema(description = "외주의뢰가")
    val outsourcingCost:Int?,
    @Schema(description = "세부 의뢰들")
    val items: List<RequestItem>?
)
