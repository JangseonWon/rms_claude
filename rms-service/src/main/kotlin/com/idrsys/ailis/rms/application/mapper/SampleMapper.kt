package com.idrsys.ailis.rms.application.mapper

import com.idrsys.ailis.rms.application.dto.request.CreateSampleCommand
import com.idrsys.ailis.rms.application.dto.request.CreateSampleTypeCommand
import com.idrsys.ailis.rms.application.dto.request.SampleCommand
import com.idrsys.ailis.rms.application.dto.response.SampleResponse
import com.idrsys.ailis.rms.application.dto.response.SampleTypeListResponse
import com.idrsys.ailis.rms.application.dto.response.SampleTypeResponse
import com.idrsys.ailis.rms.domain.model.Sample
import com.idrsys.ailis.rms.domain.model.SampleType

/**
 * Sample Mapper
 *
 * Sample Domain 모델과 DTO 간의 변환을 담당합니다.
 */
object SampleMapper {

    /**
     * CreateSampleCommand → Sample Domain
     */
    fun toDomain(command: CreateSampleCommand, sampleTypeId: Long, userId: String): Sample {
        return Sample.create(
            count = command.count,
            samplingOn = command.samplingOn,
            sampleTypeId = sampleTypeId,
            age = command.age,
            createdBy = userId
        )
    }

    /**
     * SampleCommand → Sample Domain (의뢰 생성 시)
     */
    fun toDomain(command: SampleCommand, sampleTypeId: Long, userId: String): Sample {
        return Sample.create(
            count = command.count,
            samplingOn = command.samplingOn,
            sampleTypeId = sampleTypeId,
            age = command.age,
            createdBy = userId
        )
    }

    /**
     * Sample Domain → SampleResponse
     */
    fun toResponse(sample: Sample, sampleType: SampleType): SampleResponse {
        return SampleResponse(
            id = sample.id!!,
            count = sample.count,
            age = sample.age,
            samplingOn = sample.samplingOn,
            type = SampleTypeMapper.toResponse(sampleType),
            createdAt = sample.createdAt,
            createdBy = sample.createdBy,
            updatedAt = sample.updatedAt,
            updatedBy = sample.updatedBy
        )
    }

    /**
     * Sample Domain → SampleResponse (SampleType 포함)
     */
    fun toResponse(sample: Sample): SampleResponse {
        // SampleType 정보가 필요하지만 없는 경우
        // Repository에서 함께 조회해야 함
        throw UnsupportedOperationException("SampleType이 필요합니다. toResponse(sample, sampleType)을 사용하세요.")
    }
}

/**
 * SampleType Mapper
 */
object SampleTypeMapper {

    /**
     * CreateSampleTypeCommand → SampleType Domain
     */
    fun toDomain(command: CreateSampleTypeCommand, userId: String): SampleType {
        return SampleType.create(
            serial = command.serial,
            name = command.name,
            description = command.description,
            createdBy = userId
        )
    }

    /**
     * SampleType Domain → SampleTypeResponse
     */
    fun toResponse(sampleType: SampleType): SampleTypeResponse {
        return SampleTypeResponse(
            id = sampleType.id!!,
            serial = sampleType.serial,
            name = sampleType.name,
            description = sampleType.description,
            createdAt = sampleType.createdAt,
            createdBy = sampleType.createdBy,
            updatedAt = sampleType.updatedAt,
            updatedBy = sampleType.updatedBy
        )
    }

    /**
     * SampleType Domain → SampleTypeListResponse
     */
    fun toListResponse(sampleType: SampleType): SampleTypeListResponse {
        return SampleTypeListResponse(
            id = sampleType.id!!,
            serial = sampleType.serial,
            name = sampleType.name,
            createdAt = sampleType.createdAt
        )
    }
}
