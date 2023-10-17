package com.gcgenome.rms.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Sample(
    var id: UUID?,
    val createAt: LocalDateTime?,
    val lastModifyAt: LocalDateTime?,
    val age: Int?,
    val sampling: LocalDate?,
    val note: String?,
    val genomeBarcode: String?,
    val genomeBarcodeInfix:  Int,
    val genomeBarcodePostfix: Int,
    val genomeBarcodePrefix: Int,
    val sampleBarcode: String?,
    val sampleTypeId: String,
    val department: String?,
    val ward: String?,
    val physician: String?,
    val state: String?,
    val empId: String?,
    val empName: String?,
    val empMobile: String?,
    val registrationAt: LocalDateTime?,
    val organizationId: String?,
    val itemId: UUID,
    val patientSerial: String,
    val userId: String
)
