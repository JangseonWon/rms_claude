package com.idrsys.ailis.rms.application.service

import com.idrsys.ailis.rms.application.dto.request.CreateRequestCommand
import com.idrsys.ailis.rms.application.dto.request.PatchRequestCommand
import com.idrsys.ailis.rms.application.dto.request.SearchRequestQuery
import com.idrsys.ailis.rms.application.dto.request.UpdateRequestCommand
import com.idrsys.ailis.rms.application.dto.response.RequestListResponse
import com.idrsys.ailis.rms.application.dto.response.RequestResponse
import com.idrsys.ailis.rms.application.mapper.OrganizationMapper
import com.idrsys.ailis.rms.application.mapper.PatientMapper
import com.idrsys.ailis.rms.application.mapper.RequestMapper
import com.idrsys.ailis.rms.application.mapper.SampleMapper
import com.idrsys.ailis.rms.application.mapper.SampleTypeMapper
import com.idrsys.ailis.rms.application.usecase.RequestUseCase
import com.idrsys.ailis.rms.domain.repository.*
import com.idrsys.ailis.rms.shared.exception.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 의뢰 관리 Application Service
 *
 * 의뢰 관련 비즈니스 로직을 처리합니다.
 */
@Service
@Transactional
class RequestApplicationService(
    private val requestRepository: RequestRepository,
    private val organizationRepository: OrganizationRepository,
    private val patientRepository: PatientRepository,
    private val sampleRepository: SampleRepository,
    private val sampleTypeRepository: SampleTypeRepository,
    private val extensionRepository: ExtensionRepository,
    private val requestExtensionRepository: RequestExtensionRepository
) : RequestUseCase {

    override suspend fun createRequest(command: CreateRequestCommand, userId: String): RequestResponse {
        // 1. 중복 체크
        if (requestRepository.existsBySerial(command.serial)) {
            throw DuplicateRequestException(command.serial)
        }

        // 2. Organization 조회 또는 생성
        val organization = organizationRepository.findBySerial(command.organization.serial)
            ?: throw OrganizationNotFoundException.bySerial(command.organization.serial)

        // 3. Patient 조회 또는 생성
        var patient = patientRepository.findBySerial(command.patient.serial)
        if (patient == null) {
            patient = patientRepository.save(
                PatientMapper.toDomain(command.patient, userId)
            )
        }

        // 4. SampleType 조회
        val sampleType = sampleTypeRepository.findBySerial(command.sample.type.serial)
            ?: throw SampleTypeNotFoundException.bySerial(command.sample.type.serial)

        // 5. Sample 생성
        val sample = sampleRepository.save(
            SampleMapper.toDomain(command.sample, sampleType.id!!, userId)
        )

        // 6. Request 생성
        val request = requestRepository.save(
            RequestMapper.toDomain(
                command = command,
                organizationId = organization.id!!,
                patientId = patient.id!!,
                sampleId = sample.id!!,
                userId = userId
            )
        )

        // 7. RequestExtension 생성
        val extensions = command.extensions?.map { extCmd ->
            val extension = extensionRepository.findByCode(extCmd.code)
                ?: throw ExtensionNotFoundException.byCode(extCmd.code)

            val requestExtension = requestExtensionRepository.save(
                RequestMapper.extensionToDomain(request.id!!, extCmd, userId)
            )

            extension to requestExtension
        } ?: emptyList()

        // 8. Response 반환
        return RequestMapper.toResponse(request, organization, patient, sample, extensions)
    }

    @Transactional(readOnly = true)
    override suspend fun getRequest(id: Long): RequestResponse {
        val request = requestRepository.findById(id)
            ?: throw RequestNotFoundException(id.toString())

        return buildRequestResponse(request)
    }

    @Transactional(readOnly = true)
    override suspend fun getRequestBySerial(serial: String): RequestResponse {
        val request = requestRepository.findBySerial(serial)
            ?: throw RequestNotFoundException()

        return buildRequestResponse(request)
    }

    @Transactional(readOnly = true)
    override fun getAllRequests(): Flow<RequestListResponse> {
        return requestRepository.findAll().map { request ->
            val organization = organizationRepository.findById(request.organizationId)!!
            val patient = patientRepository.findById(request.patientId)!!

            RequestMapper.toListResponse(request, organization.name, patient.name)
        }
    }

    @Transactional(readOnly = true)
    override fun searchRequests(query: SearchRequestQuery): Flow<RequestListResponse> {
        return requestRepository.findByDateRange(query.requestDateFrom, query.requestDateTo)
            .map { request ->
                val organization = organizationRepository.findById(request.organizationId)!!
                val patient = patientRepository.findById(request.patientId)!!

                RequestMapper.toListResponse(request, organization.name, patient.name)
            }
    }

    override suspend fun updateRequest(id: Long, command: UpdateRequestCommand, userId: String): RequestResponse {
        val request = requestRepository.findById(id)
            ?: throw RequestNotFoundException(id.toString())

        // 업데이트 로직 구현 (간단 버전)
        val updatedRequest = request.update(
            department = command.department,
            ward = command.ward,
            physician = command.physician,
            memo = command.memo,
            genomePrice = command.genomePrice,
            labsPrice = command.labsPrice,
            updatedBy = userId
        )

        val savedRequest = requestRepository.save(updatedRequest)
        return buildRequestResponse(savedRequest)
    }

    override suspend fun patchRequest(id: Long, command: PatchRequestCommand, userId: String): RequestResponse {
        val request = requestRepository.findById(id)
            ?: throw RequestNotFoundException(id.toString())

        // PATCH 로직은 JsonNullable 처리가 필요
        // 간단 버전으로 구현
        val updatedRequest = request.copy(
            department = if (command.department.isPresent) command.department.get() else request.department,
            ward = if (command.ward.isPresent) command.ward.get() else request.ward,
            physician = if (command.physician.isPresent) command.physician.get() else request.physician,
            memo = if (command.memo.isPresent) command.memo.get() else request.memo,
            genomePrice = if (command.genomePrice.isPresent) command.genomePrice.get() else request.genomePrice,
            labsPrice = if (command.labsPrice.isPresent) command.labsPrice.get() else request.labsPrice
        )

        val savedRequest = requestRepository.save(updatedRequest)
        return buildRequestResponse(savedRequest)
    }

    override suspend fun deleteRequest(id: Long): Boolean {
        if (!requestRepository.existsBySerial(id.toString())) {
            throw RequestNotFoundException(id.toString())
        }

        return requestRepository.deleteById(id)
    }

    /**
     * Request Response 빌드 헬퍼 메서드
     */
    private suspend fun buildRequestResponse(request: com.idrsys.ailis.rms.domain.model.Request): RequestResponse {
        val organization = organizationRepository.findById(request.organizationId)!!
        val patient = patientRepository.findById(request.patientId)!!
        val sample = sampleRepository.findById(request.sampleId)!!
        val sampleType = sampleTypeRepository.findById(sample.sampleTypeId)!!

        val extensions = mutableListOf<Pair<com.idrsys.ailis.rms.domain.model.Extension, com.idrsys.ailis.rms.domain.model.RequestExtension>>()
        requestExtensionRepository.findByRequestId(request.id!!).collect { reqExt ->
            val extension = extensionRepository.findByCode(reqExt.extensionCode)!!
            extensions.add(extension to reqExt)
        }

        return RequestMapper.toResponse(
            request = request,
            organization = organization,
            patient = patient,
            sample = sample,
            extensions = extensions
        )
    }
}
