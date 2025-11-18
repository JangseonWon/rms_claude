package com.idrsys.ailis.rms.application.usecase

import com.idrsys.ailis.rms.application.dto.request.CreatePatientCommand
import com.idrsys.ailis.rms.application.dto.request.PatchPatientCommand
import com.idrsys.ailis.rms.application.dto.request.UpdatePatientCommand
import com.idrsys.ailis.rms.application.dto.response.PatientListResponse
import com.idrsys.ailis.rms.application.dto.response.PatientResponse
import kotlinx.coroutines.flow.Flow

/**
 * 환자 관리 UseCase
 *
 * 환자 생성, 조회, 수정, 삭제 등의 비즈니스 로직을 정의합니다.
 */
interface PatientUseCase {

    /**
     * 환자 생성
     */
    suspend fun createPatient(command: CreatePatientCommand, userId: String): PatientResponse

    /**
     * 환자 조회 (ID)
     */
    suspend fun getPatient(id: Long): PatientResponse

    /**
     * 환자 조회 (일련번호)
     */
    suspend fun getPatientBySerial(serial: String): PatientResponse

    /**
     * 환자 목록 조회
     */
    fun getAllPatients(): Flow<PatientListResponse>

    /**
     * 환자명으로 검색
     */
    fun searchPatientsByName(name: String): Flow<PatientListResponse>

    /**
     * 환자 수정 (전체)
     */
    suspend fun updatePatient(id: Long, command: UpdatePatientCommand, userId: String): PatientResponse

    /**
     * 환자 수정 (부분)
     */
    suspend fun patchPatient(id: Long, command: PatchPatientCommand, userId: String): PatientResponse

    /**
     * 환자 삭제
     */
    suspend fun deletePatient(id: Long): Boolean
}
