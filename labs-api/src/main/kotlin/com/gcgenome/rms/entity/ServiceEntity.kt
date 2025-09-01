package com.gcgenome.rms.entity

import java.util.*

data class ServiceEntity(
    val id: UUID,
    val code: String,
    val nameKr: String? = null,
    val nameEr: String? = null,
    val type: String? = null,
)
