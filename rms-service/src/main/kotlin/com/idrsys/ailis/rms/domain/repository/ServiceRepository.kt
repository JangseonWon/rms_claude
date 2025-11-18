package com.idrsys.ailis.rms.domain.repository

import com.idrsys.ailis.rms.domain.model.Service
import kotlinx.coroutines.flow.Flow

/**
 * Service Repository 인터페이스
 *
 * 서비스 데이터 접근을 위한 포트(Port) 정의
 */
interface ServiceRepository {

    /**
     * 서비스 저장
     */
    suspend fun save(service: Service): Service

    /**
     * ID로 서비스 조회
     */
    suspend fun findById(id: Long): Service?

    /**
     * 일련번호로 서비스 조회
     */
    suspend fun findBySerial(serial: String): Service?

    /**
     * 카테고리 ID로 서비스 조회
     */
    fun findByCategoryId(categoryId: Long): Flow<Service>

    /**
     * 모든 서비스 조회
     */
    fun findAll(): Flow<Service>

    /**
     * 서비스 삭제
     */
    suspend fun deleteById(id: Long): Boolean

    /**
     * 일련번호 존재 여부 확인
     */
    suspend fun existsBySerial(serial: String): Boolean
}
