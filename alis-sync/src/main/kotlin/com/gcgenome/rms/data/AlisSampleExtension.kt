package com.gcgenome.rms.data

import java.time.LocalDateTime

data class AlisSampleExtension(
    val orderDate: LocalDateTime,
    val orderNumber: String,
    val extensionCode: String,
    val extensionValue: String?
)
