package com.gcgenome.rms.swagger.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "기관")
data class RequestOrganization(
    @Schema(description = "기관 ID")
    val id:String?,
    @Schema(description = "기관명")
    val name:String?,
    @Schema(description = "기관유형")
    val type:String?,
    @Schema(description = "사업자번호")
    val registrationNumber:String?,
    @Schema(description = "요양기관번호")
    val nursingNumber:String?,
    @Schema(description = "영업소 ID")
    val branchCode:String?,
    @Schema(description = "영업소명")
    val branchName:String?
)