package com.idrsys.ailis.rms.domain.model

import java.time.LocalDateTime

/**
 * Service(서비스) 도메인 모델
 *
 * 제공되는 검사 서비스 정보를 나타냅니다.
 */
data class Service(
    val id: Long? = null,
    val serial: String,
    val name: String,
    val description: String? = null,
    val price: Long? = null,
    val categoryId: Long? = null,
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
            price: Long? = null,
            categoryId: Long? = null,
            createdBy: String
        ): Service {
            require(serial.isNotBlank()) { "서비스 일련번호는 필수입니다" }
            require(name.isNotBlank()) { "서비스명은 필수입니다" }
            if (price != null) {
                require(price >= 0) { "가격은 0 이상이어야 합니다" }
            }

            return Service(
                serial = serial,
                name = name,
                description = description,
                price = price,
                categoryId = categoryId,
                createdAt = LocalDateTime.now(),
                createdBy = createdBy
            )
        }
    }

    fun update(
        name: String? = this.name,
        description: String? = this.description,
        price: Long? = this.price,
        categoryId: Long? = this.categoryId,
        updatedBy: String
    ): Service {
        if (price != null) {
            require(price >= 0) { "가격은 0 이상이어야 합니다" }
        }

        return copy(
            name = name ?: this.name,
            description = description,
            price = price,
            categoryId = categoryId,
            updatedAt = LocalDateTime.now(),
            updatedBy = updatedBy
        )
    }
}
