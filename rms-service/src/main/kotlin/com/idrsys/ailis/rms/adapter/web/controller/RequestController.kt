package com.idrsys.ailis.rms.adapter.web.controller

import com.idrsys.ailis.rms.application.dto.request.CreateRequestCommand
import com.idrsys.ailis.rms.application.dto.request.PatchRequestCommand
import com.idrsys.ailis.rms.application.dto.request.SearchRequestQuery
import com.idrsys.ailis.rms.application.dto.request.UpdateRequestCommand
import com.idrsys.ailis.rms.application.dto.response.CreatedResponse
import com.idrsys.ailis.rms.application.dto.response.DeletedResponse
import com.idrsys.ailis.rms.application.dto.response.RequestListResponse
import com.idrsys.ailis.rms.application.dto.response.RequestResponse
import com.idrsys.ailis.rms.application.usecase.RequestUseCase
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * 의뢰 관리 REST Controller
 *
 * 의뢰 생성, 조회, 수정, 삭제 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/requests")
class RequestController(
    private val requestUseCase: RequestUseCase
) {

    /**
     * 의뢰 생성
     *
     * POST /api/requests
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createRequest(
        @Valid @RequestBody command: CreateRequestCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): CreatedResponse {
        val response = requestUseCase.createRequest(command, userId)
        return CreatedResponse(
            id = response.id,
            message = "의뢰가 성공적으로 생성되었습니다"
        )
    }

    /**
     * 의뢰 조회 (ID)
     *
     * GET /api/requests/{id}
     */
    @GetMapping("/{id}")
    suspend fun getRequest(@PathVariable id: Long): RequestResponse {
        return requestUseCase.getRequest(id)
    }

    /**
     * 의뢰 조회 (일련번호)
     *
     * GET /api/requests/serial/{serial}
     */
    @GetMapping("/serial/{serial}")
    suspend fun getRequestBySerial(@PathVariable serial: String): RequestResponse {
        return requestUseCase.getRequestBySerial(serial)
    }

    /**
     * 의뢰 목록 조회
     *
     * GET /api/requests
     */
    @GetMapping
    fun getAllRequests(): Flow<RequestListResponse> {
        return requestUseCase.getAllRequests()
    }

    /**
     * 의뢰 검색
     *
     * POST /api/requests/search
     */
    @PostMapping("/search")
    fun searchRequests(@Valid @RequestBody query: SearchRequestQuery): Flow<RequestListResponse> {
        return requestUseCase.searchRequests(query)
    }

    /**
     * 의뢰 수정 (전체)
     *
     * PUT /api/requests/{id}
     */
    @PutMapping("/{id}")
    suspend fun updateRequest(
        @PathVariable id: Long,
        @Valid @RequestBody command: UpdateRequestCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): RequestResponse {
        return requestUseCase.updateRequest(id, command, userId)
    }

    /**
     * 의뢰 수정 (부분)
     *
     * PATCH /api/requests/{id}
     */
    @PatchMapping("/{id}")
    suspend fun patchRequest(
        @PathVariable id: Long,
        @Valid @RequestBody command: PatchRequestCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): RequestResponse {
        return requestUseCase.patchRequest(id, command, userId)
    }

    /**
     * 의뢰 삭제
     *
     * DELETE /api/requests/{id}
     */
    @DeleteMapping("/{id}")
    suspend fun deleteRequest(@PathVariable id: Long): DeletedResponse {
        requestUseCase.deleteRequest(id)
        return DeletedResponse(
            id = id,
            message = "의뢰가 성공적으로 삭제되었습니다"
        )
    }
}
