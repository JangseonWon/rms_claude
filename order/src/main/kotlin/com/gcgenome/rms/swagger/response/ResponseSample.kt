package com.gcgenome.rms.swagger.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.*

@Schema(description = "의뢰 별 세부 내용")
data class ResponseSample(
    @Schema(description = "검체 검체 ID")
    var id: UUID?,
    @Schema(description = "검체 종류 ID")
    val typeId: String?,
    @Schema(description = "부속거래처 ID")
    val organizationId: String?,
    @Schema(description = "MRN")
    val serial: String?,
    @Schema(description = "검체 채취 당시 수진자 나이")
    val age: Int?,
    @Schema(description = "검체 채취일")
    val sampling: LocalDate?,
    @Schema(description = "검체 특이사항 메모")
    val note: String?,
    @Schema(description = "진료과")
    val department: String?,
    @Schema(description = "병동")
    val ward: String?,
    @Schema(description = "담당교수 (의사)명")
    val physician: String?,
    @Schema(description = "검체 진행 상태")
    val state: String?,
    @Schema(description = "추가 등록 정보")
    val extensions: List<ResponseExtension>?
)
