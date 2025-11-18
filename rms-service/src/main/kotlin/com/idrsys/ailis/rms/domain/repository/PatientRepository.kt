package com.idrsys.ailis.rms.domain.repository

import com.idrsys.ailis.rms.domain.model.Patient
import kotlinx.coroutines.flow.Flow

/**
 * Patient Repository 인터페이스
 *
 * 환자 데이터 접근을 위한 포트(Port) 정의
 */
interface PatientRepository {

    /**
     * 환자 저장
     */
    suspend fun save(patient: Patient): Patient

    /**
     * ID로 환자 조회
     */
    suspend fun findById(id: Long): Patient?

    /**
     * 일련번호로 환자 조회
     */
    suspend fun findBySerial(serial: String): Patient?

    /**
     * 이름으로 환자 조회
     */
    fun findByName(name: String): Flow<Patient>

    /**
     * 모든 환자 조회
     */
    fun findAll(): Flow<Patient>

    /**
     * 환자 삭제
     */
    suspend fun deleteById(id: Long): Boolean

    /**
     * 일련번호 존재 여부 확인
     */
    suspend fun existsBySerial(serial: String): Boolean
}
