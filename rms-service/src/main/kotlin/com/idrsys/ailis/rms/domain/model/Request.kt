package com.idrsys.ailis.rms.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Request(의뢰) 도메인 모델
 *
 * 검사 의뢰의 핵심 정보를 나타냅니다.
 */
data class Request(
    val id: Long? = null,
    val serial: String,
    val requestDateFrom: LocalDate,
    val requestDateTo: LocalDate,
    val department: String? = null,
    val ward: String? = null,
    val physician: String? = null,
    val memo: String? = null,
    val genomePrice: Long? = null,
    val labsPrice: Long? = null,
    val organizationId: Long,
    val patientId: Long,
    val sampleId: Long,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null,
    val updatedAt: LocalDateTime? = null,
    val updatedBy: String? = null
) {
    /**
     * 의뢰 날짜 범위 검증
     */
    fun validateDateRange(): Boolean {
        return !requestDateFrom.isAfter(requestDateTo)
    }

    /**
     * 의뢰 생성
     */
    companion object {
        fun create(
            serial: String,
            requestDateFrom: LocalDate,
            requestDateTo: LocalDate,
            organizationId: Long,
            patientId: Long,
            sampleId: Long,
            department: String? = null,
            ward: String? = null,
            physician: String? = null,
            memo: String? = null,
            genomePrice: Long? = null,
            labsPrice: Long? = null,
            createdBy: String
        ): Request {
            require(serial.isNotBlank()) { "의뢰 일련번호는 필수입니다" }
            require(!requestDateFrom.isAfter(requestDateTo)) { "시작일은 종료일보다 이전이어야 합니다" }

            return Request(
                serial = serial,
                requestDateFrom = requestDateFrom,
                requestDateTo = requestDateTo,
                organizationId = organizationId,
                patientId = patientId,
                sampleId = sampleId,
                department = department,
                ward = ward,
                physician = physician,
                memo = memo,
                genomePrice = genomePrice,
                labsPrice = labsPrice,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }
    }

    /**
     * 의뢰 수정
     */
    fun update(
        department: String? = this.department,
        ward: String? = this.ward,
        physician: String? = this.physician,
        memo: String? = this.memo,
        genomePrice: Long? = this.genomePrice,
        labsPrice: Long? = this.labsPrice,
        updatedBy: String
    ): Request {
        return copy(
            department = department,
            ward = ward,
            physician = physician,
            memo = memo,
            genomePrice = genomePrice,
            labsPrice = labsPrice,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
    }
}
