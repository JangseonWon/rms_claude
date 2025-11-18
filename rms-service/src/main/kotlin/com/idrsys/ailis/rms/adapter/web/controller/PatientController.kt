package com.idrsys.ailis.rms.adapter.web.controller

import com.idrsys.ailis.rms.application.dto.request.CreatePatientCommand
import com.idrsys.ailis.rms.application.dto.request.PatchPatientCommand
import com.idrsys.ailis.rms.application.dto.request.UpdatePatientCommand
import com.idrsys.ailis.rms.application.dto.response.CreatedResponse
import com.idrsys.ailis.rms.application.dto.response.DeletedResponse
import com.idrsys.ailis.rms.application.dto.response.PatientListResponse
import com.idrsys.ailis.rms.application.dto.response.PatientResponse
import com.idrsys.ailis.rms.application.usecase.PatientUseCase
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * 환자 관리 REST Controller
 */
@RestController
@RequestMapping("/api/patients")
class PatientController(
    private val patientUseCase: PatientUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createPatient(
        @Valid @RequestBody command: CreatePatientCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): CreatedResponse {
        val response = patientUseCase.createPatient(command, userId)
        return CreatedResponse(
            id = response.id,
            message = "환자 정보가 성공적으로 등록되었습니다"
        )
    }

    @GetMapping("/{id}")
    suspend fun getPatient(@PathVariable id: Long): PatientResponse {
        return patientUseCase.getPatient(id)
    }

    @GetMapping("/serial/{serial}")
    suspend fun getPatientBySerial(@PathVariable serial: String): PatientResponse {
        return patientUseCase.getPatientBySerial(serial)
    }

    @GetMapping
    fun getAllPatients(): Flow<PatientListResponse> {
        return patientUseCase.getAllPatients()
    }

    @GetMapping("/search")
    fun searchPatients(@RequestParam name: String): Flow<PatientListResponse> {
        return patientUseCase.searchPatientsByName(name)
    }

    @PutMapping("/{id}")
    suspend fun updatePatient(
        @PathVariable id: Long,
        @Valid @RequestBody command: UpdatePatientCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): PatientResponse {
        return patientUseCase.updatePatient(id, command, userId)
    }

    @PatchMapping("/{id}")
    suspend fun patchPatient(
        @PathVariable id: Long,
        @Valid @RequestBody command: PatchPatientCommand,
        @RequestHeader("X-User-Id", required = false, defaultValue = "system") userId: String
    ): PatientResponse {
        return patientUseCase.patchPatient(id, command, userId)
    }

    @DeleteMapping("/{id}")
    suspend fun deletePatient(@PathVariable id: Long): DeletedResponse {
        patientUseCase.deletePatient(id)
        return DeletedResponse(
            id = id,
            message = "환자 정보가 성공적으로 삭제되었습니다"
        )
    }
}
