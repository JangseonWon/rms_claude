package com.idrsys.ailis.rms.application.usecase

import com.idrsys.ailis.rms.application.dto.request.CreateOrganizationCommand
import com.idrsys.ailis.rms.application.dto.request.PatchOrganizationCommand
import com.idrsys.ailis.rms.application.dto.request.UpdateOrganizationCommand
import com.idrsys.ailis.rms.application.dto.response.OrganizationListResponse
import com.idrsys.ailis.rms.application.dto.response.OrganizationResponse
import kotlinx.coroutines.flow.Flow

/**
 * 기관 관리 UseCase
 *
 * 기관 생성, 조회, 수정, 삭제 등의 비즈니스 로직을 정의합니다.
 */
interface OrganizationUseCase {

    /**
     * 기관 생성
     */
    suspend fun createOrganization(command: CreateOrganizationCommand, userId: String): OrganizationResponse

    /**
     * 기관 조회 (ID)
     */
    suspend fun getOrganization(id: Long): OrganizationResponse

    /**
     * 기관 조회 (일련번호)
     */
    suspend fun getOrganizationBySerial(serial: String): OrganizationResponse

    /**
     * 기관 목록 조회
     */
    fun getAllOrganizations(): Flow<OrganizationListResponse>

    /**
     * 기관명으로 검색
     */
    fun searchOrganizationsByName(name: String): Flow<OrganizationListResponse>

    /**
     * 기관 수정 (전체)
     */
    suspend fun updateOrganization(id: Long, command: UpdateOrganizationCommand, userId: String): OrganizationResponse

    /**
     * 기관 수정 (부분)
     */
    suspend fun patchOrganization(id: Long, command: PatchOrganizationCommand, userId: String): OrganizationResponse

    /**
     * 기관 삭제
     */
    suspend fun deleteOrganization(id: Long): Boolean
}
