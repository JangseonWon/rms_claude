package com.idrsys.ailis.rms.domain.repository

import com.idrsys.ailis.rms.domain.model.Extension
import com.idrsys.ailis.rms.domain.model.RequestExtension
import kotlinx.coroutines.flow.Flow

/**
 * Extension Repository 인터페이스
 *
 * 추가 정보 데이터 접근을 위한 포트(Port) 정의
 */
interface ExtensionRepository {

    /**
     * 추가 정보 저장
     */
    suspend fun save(extension: Extension): Extension

    /**
     * ID로 추가 정보 조회
     */
    suspend fun findById(id: Long): Extension?

    /**
     * 코드로 추가 정보 조회
     */
    suspend fun findByCode(code: String): Extension?

    /**
     * 모든 추가 정보 조회
     */
    fun findAll(): Flow<Extension>

    /**
     * 필수 추가 정보 조회
     */
    fun findAllRequired(): Flow<Extension>

    /**
     * 추가 정보 삭제
     */
    suspend fun deleteById(id: Long): Boolean

    /**
     * 코드 존재 여부 확인
     */
    suspend fun existsByCode(code: String): Boolean
}

/**
 * RequestExtension Repository 인터페이스
 *
 * 의뢰 추가 정보 데이터 접근을 위한 포트(Port) 정의
 */
interface RequestExtensionRepository {

    /**
     * 의뢰 추가 정보 저장
     */
    suspend fun save(requestExtension: RequestExtension): RequestExtension

    /**
     * ID로 의뢰 추가 정보 조회
     */
    suspend fun findById(id: Long): RequestExtension?

    /**
     * 의뢰 ID로 의뢰 추가 정보 조회
     */
    fun findByRequestId(requestId: Long): Flow<RequestExtension>

    /**
     * 의뢰 ID와 코드로 의뢰 추가 정보 조회
     */
    suspend fun findByRequestIdAndCode(requestId: Long, extensionCode: String): RequestExtension?

    /**
     * 의뢰 추가 정보 삭제
     */
    suspend fun deleteById(id: Long): Boolean

    /**
     * 의뢰 ID로 의뢰 추가 정보 전체 삭제
     */
    suspend fun deleteByRequestId(requestId: Long): Int
}
