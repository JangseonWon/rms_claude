package com.idrsys.ailis.rms.application.dto.response

import java.time.LocalDateTime

/**
 * 페이지네이션 응답 DTO
 */
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

/**
 * 생성 성공 응답 DTO
 */
data class CreatedResponse(
    val id: Long,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * 수정 성공 응답 DTO
 */
data class UpdatedResponse(
    val id: Long,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * 삭제 성공 응답 DTO
 */
data class DeletedResponse(
    val id: Long,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * 일반 성공 응답 DTO
 */
data class SuccessResponse(
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
