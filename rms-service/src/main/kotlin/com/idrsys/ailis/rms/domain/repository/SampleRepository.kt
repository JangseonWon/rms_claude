package com.idrsys.ailis.rms.domain.repository

import com.idrsys.ailis.rms.domain.model.Sample
import com.idrsys.ailis.rms.domain.model.SampleType
import kotlinx.coroutines.flow.Flow

/**
 * Sample Repository 인터페이스
 *
 * 샘플 데이터 접근을 위한 포트(Port) 정의
 */
interface SampleRepository {

    /**
     * 샘플 저장
     */
    suspend fun save(sample: Sample): Sample

    /**
     * ID로 샘플 조회
     */
    suspend fun findById(id: Long): Sample?

    /**
     * 샘플 유형 ID로 샘플 조회
     */
    fun findBySampleTypeId(sampleTypeId: Long): Flow<Sample>

    /**
     * 모든 샘플 조회
     */
    fun findAll(): Flow<Sample>

    /**
     * 샘플 삭제
     */
    suspend fun deleteById(id: Long): Boolean
}

/**
 * SampleType Repository 인터페이스
 *
 * 샘플 유형 데이터 접근을 위한 포트(Port) 정의
 */
interface SampleTypeRepository {

    /**
     * 샘플 유형 저장
     */
    suspend fun save(sampleType: SampleType): SampleType

    /**
     * ID로 샘플 유형 조회
     */
    suspend fun findById(id: Long): SampleType?

    /**
     * 일련번호로 샘플 유형 조회
     */
    suspend fun findBySerial(serial: String): SampleType?

    /**
     * 모든 샘플 유형 조회
     */
    fun findAll(): Flow<SampleType>

    /**
     * 샘플 유형 삭제
     */
    suspend fun deleteById(id: Long): Boolean

    /**
     * 일련번호 존재 여부 확인
     */
    suspend fun existsBySerial(serial: String): Boolean
}
