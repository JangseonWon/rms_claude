package com.gcgenome.rms.data

import com.gcgenome.rms.tables.pojos.Patient
import com.gcgenome.rms.tables.pojos.Sample
import com.gcgenome.rms.tables.pojos.Service
import java.time.LocalDateTime
import java.util.*

data class SelectRequest (
    val orderId: UUID,
    val sample: Sample,
    val service: Service,
    val patient: Patient,
    val cartAt: LocalDateTime?,
    val completeAt: LocalDateTime?,
    val createAt: LocalDateTime?,
    val credit: Boolean?,
    val department: String?,
    val empId: String?,
    val empMobile: String?,
    val empName: String?,
    val lastModifyAt: LocalDateTime,
    val memo: String?,
    val outsourcingCost: Int?,
    val physician: String?,
    val price: Int?,
    val resampleAt: LocalDateTime?,
    val specifiedAt: LocalDateTime?,
    val status: String,
    val test: Boolean?,
    val userServiceId: String,
    val ward: String?
)