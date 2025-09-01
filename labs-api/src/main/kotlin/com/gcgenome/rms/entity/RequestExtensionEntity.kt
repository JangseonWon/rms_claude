package com.gcgenome.rms.entity

import java.util.*

data class RequestExtensionEntity(
    val id: UUID,
    val value: String,
    val extensionId: UUID,
    val requestId: UUID
)
