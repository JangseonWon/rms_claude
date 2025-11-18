package com.idrsys.ailis.rms.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Patient(환자) 도메인 모델
 *
 * 검사 대상 환자의 정보를 나타냅니다.
 */
data class Patient(
    val id: Long? = null,
    val serial: String,
    val name: String,
    val sex: Sex? = null,
    val age: Int? = null,
    val birth: LocalDate? = null,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null,
    val updatedAt: LocalDateTime? = null,
    val updatedBy: String? = null
) {
    companion object {
        fun create(
            serial: String,
            name: String,
            sex: Sex? = null,
            age: Int? = null,
            birth: LocalDate? = null,
            createdBy: String
        ): Patient {
            require(serial.isNotBlank()) { "환자 일련번호는 필수입니다" }
            require(name.isNotBlank()) { "환자명은 필수입니다" }
            if (age != null) {
                require(age > 0) { "나이는 양수여야 합니다" }
            }
            if (birth != null) {
                require(!birth.isAfter(LocalDate.now())) { "생년월일은 현재 날짜 이전이어야 합니다" }
            }

            return Patient(
                serial = serial,
                name = name,
                sex = sex,
                age = age,
                birth = birth,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }
    }

    fun update(
        name: String? = this.name,
        sex: Sex? = this.sex,
        age: Int? = this.age,
        birth: LocalDate? = this.birth,
        updatedBy: String
    ): Patient {
        if (age != null) {
            require(age > 0) { "나이는 양수여야 합니다" }
        }
        if (birth != null) {
            require(!birth.isAfter(LocalDate.now())) { "생년월일은 현재 날짜 이전이어야 합니다" }
        }

        return copy(
            name = name ?: this.name,
            sex = sex,
            age = age,
            birth = birth,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
    }
}

/**
 * 성별 Enum
 */
enum class Sex(val code: String, val description: String) {
    MALE("M", "남성"),
    FEMALE("F", "여성");

    companion object {
        fun fromCode(code: String): Sex {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("잘못된 성별 코드입니다: $code")
        }
    }
}
