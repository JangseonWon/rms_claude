package com.gcgenome.rms.swagger.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "수진자")
data class RequestPatient(
    @Schema(description = "MRN")
    val serial:String,
    @Schema(description = "성별")
    val sex:String?,
    @Schema(description = "수진자명")
    val name:String?,
    @Schema(description = "생년")
    val birthYear:Int?,
    @Schema(description = "생월")
    val birthMonth:Int?,
    @Schema(description = "생일")
    val birthDay:Int?,
    @Schema(description = "기관")
    val organization: RequestOrganization,
    @Schema(description = "의뢰 별 세부 내용")
    val samples: RequestSample?
)
