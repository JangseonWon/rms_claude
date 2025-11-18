package com.idrsys.ailis.rms.domain.repository

import com.idrsys.ailis.rms.domain.model.Organization
import kotlinx.coroutines.flow.Flow

/**
 * Organization Repository 인터페이스
 *
 * 기관 데이터 접근을 위한 포트(Port) 정의
 */
interface OrganizationRepository {

    /**
     * 기관 저장
     */
    suspend fun save(organization: Organization): Organization

    /**
     * ID로 기관 조회
     */
    suspend fun findById(id: Long): Organization?

    /**
     * 일련번호로 기관 조회
     */
    suspend fun findBySerial(serial: String): Organization?

    /**
     * 기관명으로 기관 조회
     */
    fun findByName(name: String): Flow<Organization>

    /**
     * 모든 기관 조회
     */
    fun findAll(): Flow<Organization>

    /**
     * 기관 삭제
     */
    suspend fun deleteById(id: Long): Boolean

    /**
     * 일련번호 존재 여부 확인
     */
    suspend fun existsBySerial(serial: String): Boolean

    /**
     * 기관에 의뢰가 존재하는지 확인
     */
    suspend fun hasRequests(organizationId: Long): Boolean
}
