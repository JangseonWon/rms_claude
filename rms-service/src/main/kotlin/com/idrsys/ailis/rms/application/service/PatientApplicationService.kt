package com.idrsys.ailis.rms.application.service

import com.idrsys.ailis.rms.application.dto.request.CreatePatientCommand
import com.idrsys.ailis.rms.application.dto.request.PatchPatientCommand
import com.idrsys.ailis.rms.application.dto.request.UpdatePatientCommand
import com.idrsys.ailis.rms.application.dto.response.PatientListResponse
import com.idrsys.ailis.rms.application.dto.response.PatientResponse
import com.idrsys.ailis.rms.application.mapper.PatientMapper
import com.idrsys.ailis.rms.application.usecase.PatientUseCase
import com.idrsys.ailis.rms.domain.model.Sex
import com.idrsys.ailis.rms.domain.repository.PatientRepository
import com.idrsys.ailis.rms.shared.exception.DuplicatePatientException
import com.idrsys.ailis.rms.shared.exception.PatientNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 환자 관리 Application Service
 */
@Service
@Transactional
class PatientApplicationService(
    private val patientRepository: PatientRepository
) : PatientUseCase {

    override suspend fun createPatient(command: CreatePatientCommand, userId: String): PatientResponse {
        // 중복 체크
        if (patientRepository.existsBySerial(command.serial)) {
            throw DuplicatePatientException(command.serial)
        }

        val patient = patientRepository.save(
            PatientMapper.toDomain(command, userId)
        )

        return PatientMapper.toResponse(patient)
    }

    @Transactional(readOnly = true)
    override suspend fun getPatient(id: Long): PatientResponse {
        val patient = patientRepository.findById(id)
            ?: throw PatientNotFoundException(id.toString())

        return PatientMapper.toResponse(patient)
    }

    @Transactional(readOnly = true)
    override suspend fun getPatientBySerial(serial: String): PatientResponse {
        val patient = patientRepository.findBySerial(serial)
            ?: throw PatientNotFoundException.bySerial(serial)

        return PatientMapper.toResponse(patient)
    }

    @Transactional(readOnly = true)
    override fun getAllPatients(): Flow<PatientListResponse> {
        return patientRepository.findAll()
            .map { PatientMapper.toListResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun searchPatientsByName(name: String): Flow<PatientListResponse> {
        return patientRepository.findByName(name)
            .map { PatientMapper.toListResponse(it) }
    }

    override suspend fun updatePatient(id: Long, command: UpdatePatientCommand, userId: String): PatientResponse {
        val patient = patientRepository.findById(id)
            ?: throw PatientNotFoundException(id.toString())

        val updatedPatient = patient.update(
            name = command.name,
            sex = command.sex?.let { Sex.fromCode(it) },
            age = command.age,
            birth = command.birth,
            updatedBy = userId
        )

        val saved = patientRepository.save(updatedPatient)
        return PatientMapper.toResponse(saved)
    }

    override suspend fun patchPatient(id: Long, command: PatchPatientCommand, userId: String): PatientResponse {
        val patient = patientRepository.findById(id)
            ?: throw PatientNotFoundException(id.toString())

        val updatedPatient = patient.copy(
            name = if (command.name.isPresent) command.name.get() else patient.name,
            sex = if (command.sex.isPresent) command.sex.get()?.let { Sex.fromCode(it) } else patient.sex,
            age = if (command.age.isPresent) command.age.get() else patient.age,
            birth = if (command.birth.isPresent) command.birth.get() else patient.birth
        )

        val saved = patientRepository.save(updatedPatient)
        return PatientMapper.toResponse(saved)
    }

    override suspend fun deletePatient(id: Long): Boolean {
        val patient = patientRepository.findById(id)
            ?: throw PatientNotFoundException(id.toString())

        return patientRepository.deleteById(id)
    }
}
