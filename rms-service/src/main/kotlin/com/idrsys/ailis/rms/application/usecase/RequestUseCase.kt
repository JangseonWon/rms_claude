package com.idrsys.ailis.rms.application.usecase

import com.idrsys.ailis.rms.application.dto.request.CreateRequestCommand
import com.idrsys.ailis.rms.application.dto.request.PatchRequestCommand
import com.idrsys.ailis.rms.application.dto.request.SearchRequestQuery
import com.idrsys.ailis.rms.application.dto.request.UpdateRequestCommand
import com.idrsys.ailis.rms.application.dto.response.RequestListResponse
import com.idrsys.ailis.rms.application.dto.response.RequestResponse
import kotlinx.coroutines.flow.Flow

/**
 * 의뢰 관리 UseCase
 *
 * 의뢰 생성, 조회, 수정, 삭제 등의 비즈니스 로직을 정의합니다.
 */
interface RequestUseCase {

    /**
     * 의뢰 생성
     */
    suspend fun createRequest(command: CreateRequestCommand, userId: String): RequestResponse

    /**
     * 의뢰 조회 (ID)
     */
    suspend fun getRequest(id: Long): RequestResponse

    /**
     * 의뢰 조회 (일련번호)
     */
    suspend fun getRequestBySerial(serial: String): RequestResponse

    /**
     * 의뢰 목록 조회
     */
    fun getAllRequests(): Flow<RequestListResponse>

    /**
     * 의뢰 검색
     */
    fun searchRequests(query: SearchRequestQuery): Flow<RequestListResponse>

    /**
     * 의뢰 수정 (전체)
     */
    suspend fun updateRequest(id: Long, command: UpdateRequestCommand, userId: String): RequestResponse

    /**
     * 의뢰 수정 (부분)
     */
    suspend fun patchRequest(id: Long, command: PatchRequestCommand, userId: String): RequestResponse

    /**
     * 의뢰 삭제
     */
    suspend fun deleteRequest(id: Long): Boolean
}
