package com.idrsys.ailis.rms.application.service

import com.idrsys.ailis.rms.application.dto.request.CreateSampleCommand
import com.idrsys.ailis.rms.application.dto.request.CreateSampleTypeCommand
import com.idrsys.ailis.rms.application.dto.request.UpdateSampleCommand
import com.idrsys.ailis.rms.application.dto.request.UpdateSampleTypeCommand
import com.idrsys.ailis.rms.application.dto.response.SampleResponse
import com.idrsys.ailis.rms.application.dto.response.SampleTypeListResponse
import com.idrsys.ailis.rms.application.dto.response.SampleTypeResponse
import com.idrsys.ailis.rms.application.mapper.SampleMapper
import com.idrsys.ailis.rms.application.mapper.SampleTypeMapper
import com.idrsys.ailis.rms.application.usecase.SampleTypeUseCase
import com.idrsys.ailis.rms.application.usecase.SampleUseCase
import com.idrsys.ailis.rms.domain.repository.SampleRepository
import com.idrsys.ailis.rms.domain.repository.SampleTypeRepository
import com.idrsys.ailis.rms.shared.exception.DuplicateSampleTypeException
import com.idrsys.ailis.rms.shared.exception.SampleNotFoundException
import com.idrsys.ailis.rms.shared.exception.SampleTypeNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 샘플 관리 Application Service
 */
@Service
@Transactional
class SampleApplicationService(
    private val sampleRepository: SampleRepository,
    private val sampleTypeRepository: SampleTypeRepository
) : SampleUseCase {

    override suspend fun createSample(command: CreateSampleCommand, userId: String): SampleResponse {
        val sampleType = sampleTypeRepository.findBySerial(command.type.serial)
            ?: throw SampleTypeNotFoundException.bySerial(command.type.serial)

        val sample = sampleRepository.save(
            SampleMapper.toDomain(command, sampleType.id!!, userId)
        )

        return SampleMapper.toResponse(sample, sampleType)
    }

    @Transactional(readOnly = true)
    override suspend fun getSample(id: Long): SampleResponse {
        val sample = sampleRepository.findById(id)
            ?: throw SampleNotFoundException(id.toString())

        val sampleType = sampleTypeRepository.findById(sample.sampleTypeId)!!

        return SampleMapper.toResponse(sample, sampleType)
    }

    override suspend fun updateSample(id: Long, command: UpdateSampleCommand, userId: String): SampleResponse {
        val sample = sampleRepository.findById(id)
            ?: throw SampleNotFoundException(id.toString())

        val sampleType = sampleTypeRepository.findBySerial(command.type.serial)
            ?: throw SampleTypeNotFoundException.bySerial(command.type.serial)

        val updatedSample = sample.update(
            count = command.count,
            age = command.age,
            samplingOn = command.samplingOn,
            sampleTypeId = sampleType.id!!,
            updatedBy = userId
        )

        val saved = sampleRepository.save(updatedSample)
        return SampleMapper.toResponse(saved, sampleType)
    }

    override suspend fun deleteSample(id: Long): Boolean {
        val sample = sampleRepository.findById(id)
            ?: throw SampleNotFoundException(id.toString())

        return sampleRepository.deleteById(id)
    }
}

/**
 * 샘플 유형 관리 Application Service
 */
@Service
@Transactional
class SampleTypeApplicationService(
    private val sampleTypeRepository: SampleTypeRepository
) : SampleTypeUseCase {

    override suspend fun createSampleType(command: CreateSampleTypeCommand, userId: String): SampleTypeResponse {
        // 중복 체크
        if (sampleTypeRepository.existsBySerial(command.serial)) {
            throw DuplicateSampleTypeException()
        }

        val sampleType = sampleTypeRepository.save(
            SampleTypeMapper.toDomain(command, userId)
        )

        return SampleTypeMapper.toResponse(sampleType)
    }

    @Transactional(readOnly = true)
    override suspend fun getSampleType(id: Long): SampleTypeResponse {
        val sampleType = sampleTypeRepository.findById(id)
            ?: throw SampleTypeNotFoundException(id.toString())

        return SampleTypeMapper.toResponse(sampleType)
    }

    @Transactional(readOnly = true)
    override suspend fun getSampleTypeBySerial(serial: String): SampleTypeResponse {
        val sampleType = sampleTypeRepository.findBySerial(serial)
            ?: throw SampleTypeNotFoundException.bySerial(serial)

        return SampleTypeMapper.toResponse(sampleType)
    }

    @Transactional(readOnly = true)
    override fun getAllSampleTypes(): Flow<SampleTypeListResponse> {
        return sampleTypeRepository.findAll()
            .map { SampleTypeMapper.toListResponse(it) }
    }

    override suspend fun updateSampleType(id: Long, command: UpdateSampleTypeCommand, userId: String): SampleTypeResponse {
        val sampleType = sampleTypeRepository.findById(id)
            ?: throw SampleTypeNotFoundException(id.toString())

        val updatedSampleType = sampleType.update(
            name = command.name,
            description = command.description,
            updatedBy = userId
        )

        val saved = sampleTypeRepository.save(updatedSampleType)
        return SampleTypeMapper.toResponse(saved)
    }

    override suspend fun deleteSampleType(id: Long): Boolean {
        val sampleType = sampleTypeRepository.findById(id)
            ?: throw SampleTypeNotFoundException(id.toString())

        return sampleTypeRepository.deleteById(id)
    }
}
