package com.gcgenome.lims.data

import java.time.LocalDateTime
import java.util.*

data class Report(
    var id: UUID?,
    var type: String?,
    var value: String?,
    var createAt: LocalDateTime?,
    var reportedAt: LocalDateTime?,
    var isLatest: Boolean?,
    var orderId: UUID?,
    var serviceId: String?,
    var sampleId: UUID?
    )
