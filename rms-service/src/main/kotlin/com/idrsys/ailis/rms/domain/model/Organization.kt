package com.idrsys.ailis.rms.domain.model

import java.time.LocalDateTime

/**
 * Organization(기관) 도메인 모델
 *
 * 의뢰를 요청하는 의료 기관 정보를 나타냅니다.
 */
data class Organization(
    val id: Long? = null,
    val serial: String,
    val name: String,
    val registrationNumber: String? = null,
    val nursingNumber: String? = null,
    val branchCode: String? = null,
    val branchName: String? = null,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val employeePhone: String? = null,
    val type: String? = null,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null,
    val updatedAt: LocalDateTime? = null,
    val updatedBy: String? = null
) {
    companion object {
        fun create(
            serial: String,
            name: String,
            registrationNumber: String? = null,
            nursingNumber: String? = null,
            branchCode: String? = null,
            branchName: String? = null,
            employeeId: String? = null,
            employeeName: String? = null,
            employeePhone: String? = null,
            type: String? = null,
            createdBy: String
        ): Organization {
            require(serial.isNotBlank()) { "기관 일련번호는 필수입니다" }
            require(name.isNotBlank()) { "기관명은 필수입니다" }

            return Organization(
                serial = serial,
                name = name,
                registrationNumber = registrationNumber,
                nursingNumber = nursingNumber,
                branchCode = branchCode,
                branchName = branchName,
                employeeId = employeeId,
                employeeName = employeeName,
                employeePhone = employeePhone,
                type = type,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }
    }

    fun update(
        name: String? = this.name,
        registrationNumber: String? = this.registrationNumber,
        nursingNumber: String? = this.nursingNumber,
        branchCode: String? = this.branchCode,
        branchName: String? = this.branchName,
        employeeId: String? = this.employeeId,
        employeeName: String? = this.employeeName,
        employeePhone: String? = this.employeePhone,
        type: String? = this.type,
        updatedBy: String
    ): Organization {
        return copy(
            name = name ?: this.name,
            registrationNumber = registrationNumber,
            nursingNumber = nursingNumber,
            branchCode = branchCode,
            branchName = branchName,
            employeeId = employeeId,
            employeeName = employeeName,
            employeePhone = employeePhone,
            type = type,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
    }
}
