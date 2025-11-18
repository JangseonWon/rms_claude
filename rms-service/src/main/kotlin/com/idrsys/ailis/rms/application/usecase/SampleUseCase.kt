package com.idrsys.ailis.rms.application.usecase

import com.idrsys.ailis.rms.application.dto.request.CreateSampleCommand
import com.idrsys.ailis.rms.application.dto.request.CreateSampleTypeCommand
import com.idrsys.ailis.rms.application.dto.request.UpdateSampleCommand
import com.idrsys.ailis.rms.application.dto.request.UpdateSampleTypeCommand
import com.idrsys.ailis.rms.application.dto.response.SampleResponse
import com.idrsys.ailis.rms.application.dto.response.SampleTypeListResponse
import com.idrsys.ailis.rms.application.dto.response.SampleTypeResponse
import kotlinx.coroutines.flow.Flow

/**
 * 샘플 관리 UseCase
 *
 * 샘플 및 샘플 유형 생성, 조회, 수정, 삭제 등의 비즈니스 로직을 정의합니다.
 */
interface SampleUseCase {

    /**
     * 샘플 생성
     */
    suspend fun createSample(command: CreateSampleCommand, userId: String): SampleResponse

    /**
     * 샘플 조회 (ID)
     */
    suspend fun getSample(id: Long): SampleResponse

    /**
     * 샘플 수정
     */
    suspend fun updateSample(id: Long, command: UpdateSampleCommand, userId: String): SampleResponse

    /**
     * 샘플 삭제
     */
    suspend fun deleteSample(id: Long): Boolean
}

/**
 * 샘플 유형 관리 UseCase
 */
interface SampleTypeUseCase {

    /**
     * 샘플 유형 생성
     */
    suspend fun createSampleType(command: CreateSampleTypeCommand, userId: String): SampleTypeResponse

    /**
     * 샘플 유형 조회 (ID)
     */
    suspend fun getSampleType(id: Long): SampleTypeResponse

    /**
     * 샘플 유형 조회 (일련번호)
     */
    suspend fun getSampleTypeBySerial(serial: String): SampleTypeResponse

    /**
     * 샘플 유형 목록 조회
     */
    fun getAllSampleTypes(): Flow<SampleTypeListResponse>

    /**
     * 샘플 유형 수정
     */
    suspend fun updateSampleType(id: Long, command: UpdateSampleTypeCommand, userId: String): SampleTypeResponse

    /**
     * 샘플 유형 삭제
     */
    suspend fun deleteSampleType(id: Long): Boolean
}
