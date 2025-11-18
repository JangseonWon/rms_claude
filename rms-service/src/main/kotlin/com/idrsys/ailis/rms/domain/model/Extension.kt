package com.idrsys.ailis.rms.domain.model

import java.time.LocalDateTime

/**
 * Extension(추가 정보) 도메인 모델
 *
 * 의뢰에 대한 추가 정보를 저장합니다.
 */
data class Extension(
    val id: Long? = null,
    val code: String,
    val name: String,
    val description: String? = null,
    val dataType: ExtensionDataType = ExtensionDataType.STRING,
    val isRequired: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null,
    val updatedAt: LocalDateTime? = null,
    val updatedBy: String? = null
) {
    companion object {
        fun create(
            code: String,
            name: String,
            description: String? = null,
            dataType: ExtensionDataType = ExtensionDataType.STRING,
            isRequired: Boolean = false,
            createdBy: String
        ): Extension {
            require(code.isNotBlank()) { "추가 정보 코드는 필수입니다" }
            require(name.isNotBlank()) { "추가 정보명은 필수입니다" }

            return Extension(
                code = code,
                name = name,
                description = description,
                dataType = dataType,
                isRequired = isRequired,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }
    }

    fun update(
        name: String? = this.name,
        description: String? = this.description,
        dataType: ExtensionDataType? = this.dataType,
        isRequired: Boolean? = this.isRequired,
        updatedBy: String
    ): Extension {
        return copy(
            name = name ?: this.name,
            description = description,
            dataType = dataType ?: this.dataType,
            isRequired = isRequired ?: this.isRequired,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
    }
}

/**
 * RequestExtension(의뢰 추가 정보) 도메인 모델
 *
 * 특정 의뢰에 대한 추가 정보 값을 저장합니다.
 */
data class RequestExtension(
    val id: Long? = null,
    val requestId: Long,
    val extensionCode: String,
    val value: String,
    val createdAt: LocalDateTime? = null,
    val createdBy: String? = null
) {
    companion object {
        fun create(
            requestId: Long,
            extensionCode: String,
            value: String,
            createdBy: String
        ): RequestExtension {
            require(extensionCode.isNotBlank()) { "추가 정보 코드는 필수입니다" }

            return RequestExtension(
                requestId = requestId,
                extensionCode = extensionCode,
                value = value,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }
    }
}

/**
 * 추가 정보 데이터 타입
 */
enum class ExtensionDataType(val description: String) {
    STRING("문자열"),
    INTEGER("정수"),
    DECIMAL("소수"),
    DATE("날짜"),
    DATETIME("날짜시간"),
    BOOLEAN("참/거짓");

    companion object {
        fun fromName(name: String): ExtensionDataType {
            return entries.find { it.name == name }
                ?: throw IllegalArgumentException("잘못된 데이터 타입입니다: $name")
        }
    }
}
