package com.idrsys.ailis.rms.application.mapper

import com.idrsys.ailis.rms.application.dto.request.CreateRequestCommand
import com.idrsys.ailis.rms.application.dto.request.RequestExtensionCommand
import com.idrsys.ailis.rms.application.dto.response.RequestExtensionResponse
import com.idrsys.ailis.rms.application.dto.response.RequestListResponse
import com.idrsys.ailis.rms.application.dto.response.RequestResponse
import com.idrsys.ailis.rms.domain.model.Extension
import com.idrsys.ailis.rms.domain.model.Organization
import com.idrsys.ailis.rms.domain.model.Patient
import com.idrsys.ailis.rms.domain.model.Request
import com.idrsys.ailis.rms.domain.model.RequestExtension
import com.idrsys.ailis.rms.domain.model.Sample

/**
 * Request Mapper
 *
 * Request Domain 모델과 DTO 간의 변환을 담당합니다.
 */
object RequestMapper {

    /**
     * CreateRequestCommand → Request Domain
     */
    fun toDomain(
        command: CreateRequestCommand,
        organizationId: Long,
        patientId: Long,
        sampleId: Long,
        userId: String
    ): Request {
        return Request.create(
            serial = command.serial,
            requestDateFrom = command.requestDateFrom,
            requestDateTo = command.requestDateTo,
            organizationId = organizationId,
            patientId = patientId,
            sampleId = sampleId,
            department = command.department,
            ward = command.ward,
            physician = command.physician,
            memo = command.memo,
            genomePrice = command.genomePrice,
            labsPrice = command.labsPrice,
            createdBy = userId
        )
    }

    /**
     * Request Domain → RequestResponse
     */
    fun toResponse(
        request: Request,
        organization: Organization,
        patient: Patient,
        sample: Sample,
        extensions: List<Pair<Extension, RequestExtension>>
    ): RequestResponse {
        return RequestResponse(
            id = request.id!!,
            serial = request.serial,
            requestDateFrom = request.requestDateFrom,
            requestDateTo = request.requestDateTo,
            department = request.department,
            ward = request.ward,
            physician = request.physician,
            memo = request.memo,
            genomePrice = request.genomePrice,
            labsPrice = request.labsPrice,
            organization = OrganizationMapper.toResponse(organization),
            patient = PatientMapper.toResponse(patient),
            sample = SampleMapper.toResponse(sample),
            extensions = extensions.map { (ext, reqExt) ->
                RequestExtensionResponse(
                    code = ext.code,
                    name = ext.name,
                    value = reqExt.value
                )
            },
            createdAt = request.createdAt,
            createdBy = request.createdBy,
            updatedAt = request.updatedAt,
            updatedBy = request.updatedBy
        )
    }

    /**
     * Request Domain → RequestListResponse
     */
    fun toListResponse(
        request: Request,
        organizationName: String,
        patientName: String
    ): RequestListResponse {
        return RequestListResponse(
            id = request.id!!,
            serial = request.serial,
            requestDateFrom = request.requestDateFrom,
            requestDateTo = request.requestDateTo,
            organizationName = organizationName,
            patientName = patientName,
            createdAt = request.createdAt
        )
    }

    /**
     * RequestExtensionCommand → RequestExtension Domain
     */
    fun extensionToDomain(
        requestId: Long,
        command: RequestExtensionCommand,
        userId: String
    ): RequestExtension {
        return RequestExtension.create(
            requestId = requestId,
            extensionCode = command.code,
            value = command.value,
            createdBy = userId
        )
    }
}
