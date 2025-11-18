package com.idrsys.ailis.rms.domain.repository

import com.idrsys.ailis.rms.domain.model.Request
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Request Repository 인터페이스
 *
 * 의뢰 데이터 접근을 위한 포트(Port) 정의
 */
interface RequestRepository {

    /**
     * 의뢰 저장
     */
    suspend fun save(request: Request): Request

    /**
     * ID로 의뢰 조회
     */
    suspend fun findById(id: Long): Request?

    /**
     * 일련번호로 의뢰 조회
     */
    suspend fun findBySerial(serial: String): Request?

    /**
     * 모든 의뢰 조회
     */
    fun findAll(): Flow<Request>

    /**
     * 기관 ID로 의뢰 조회
     */
    fun findByOrganizationId(organizationId: Long): Flow<Request>

    /**
     * 환자 ID로 의뢰 조회
     */
    fun findByPatientId(patientId: Long): Flow<Request>

    /**
     * 날짜 범위로 의뢰 조회
     */
    fun findByDateRange(dateFrom: LocalDate, dateTo: LocalDate): Flow<Request>

    /**
     * 의뢰 삭제
     */
    suspend fun deleteById(id: Long): Boolean

    /**
     * 일련번호 존재 여부 확인
     */
    suspend fun existsBySerial(serial: String): Boolean
}
