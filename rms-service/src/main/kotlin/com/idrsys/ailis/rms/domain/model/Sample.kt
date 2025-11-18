package com.idrsys.ailis.rms.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Sample(샘플) 도메인 모델
 *
 * 검사를 위한 샘플 정보를 나타냅니다.
 */
data class Sample(
    val id: Long? = null,
    val count: Int,
    val age: Int? = null,
    val samplingOn: LocalDate,
    val sampleTypeId: Long,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null,
    val updatedAt: LocalDateTime? = null,
    val updatedBy: String? = null
) {
    companion object {
        fun create(
            count: Int,
            samplingOn: LocalDate,
            sampleTypeId: Long,
            age: Int? = null,
            createdBy: String
        ): Sample {
            require(count > 0) { "샘플 수량은 1개 이상이어야 합니다" }
            require(!samplingOn.isAfter(LocalDate.now())) { "채취일은 현재 날짜 이전이어야 합니다" }
            if (age != null) {
                require(age >= 0) { "샘플 나이는 0 이상이어야 합니다" }
            }

            return Sample(
                count = count,
                samplingOn = samplingOn,
                sampleTypeId = sampleTypeId,
                age = age,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }
    }

    fun update(
        count: Int? = this.count,
        age: Int? = this.age,
        samplingOn: LocalDate? = this.samplingOn,
        sampleTypeId: Long? = this.sampleTypeId,
        updatedBy: String
    ): Sample {
        if (count != null) {
            require(count > 0) { "샘플 수량은 1개 이상이어야 합니다" }
        }
        if (samplingOn != null) {
            require(!samplingOn.isAfter(LocalDate.now())) { "채취일은 현재 날짜 이전이어야 합니다" }
        }
        if (age != null) {
            require(age >= 0) { "샘플 나이는 0 이상이어야 합니다" }
        }

        return copy(
            count = count ?: this.count,
            age = age,
            samplingOn = samplingOn ?: this.samplingOn,
            sampleTypeId = sampleTypeId ?: this.sampleTypeId,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
    }
}

/**
 * SampleType(샘플 유형) 도메인 모델
 */
data class SampleType(
    val id: Long? = null,
    val serial: String,
    val name: String,
    val description: String? = null,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null,
    val updatedAt: LocalDateTime? = null,
    val updatedBy: String? = null
) {
    companion object {
        fun create(
            serial: String,
            name: String,
            description: String? = null,
            createdBy: String
        ): SampleType {
            require(serial.isNotBlank()) { "샘플 유형 일련번호는 필수입니다" }
            require(name.isNotBlank()) { "샘플 유형명은 필수입니다" }

            return SampleType(
                serial = serial,
                name = name,
                description = description,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }
    }

    fun update(
        name: String? = this.name,
        description: String? = this.description,
        updatedBy: String
    ): SampleType {
        return copy(
            name = name ?: this.name,
            description = description,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
    }
}
