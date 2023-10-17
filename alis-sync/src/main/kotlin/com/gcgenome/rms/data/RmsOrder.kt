package com.gcgenome.rms.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class RmsOrder (
    //변환 값
    val genomeBarcode: String,
    //RMS Table
    val orderNumber: Int,
    val orderId: UUID,
    val itemId: UUID,
    val sampleId: UUID,
    val serviceId: String,
    val createAt: LocalDateTime,
    val organizationId: String?,
    val organizationName: String?,
    val organizationUserId: String?,
    val sampling: LocalDateTime?,
    val type: String?,
    val sampleTypeId: String?,
    val patientName: String?,
    val patientAge: Int?,
    val patientSex: String?,
    val mrn: String?,
    val userId: String?,
    val state: String?,
    val empId: String?,
    val empName: String?,
    val empMobile: String?,
    val serial: String?,
    val birthYear: Short?,
    val birthMonth: Byte?,
    val birthDay: Byte?,
    val price: Int?,
    val registrationNumber: String?,
    val nursingNumber: String?,
    val outsourcingCost: Int?,
    val department: String?,
    val ward: String?,
    val physician: String?,
    val note: String?,
    val branchId: String?,
    val branchName: String?,
    val sampleBarcode: String?,
    val genomeBarcodePrefix: Int?,
    val genomeBarcodeInfix: Short?,
    val genomeBarcodePostfix: Int?,
    val userCode: Short?
) {
    companion object {
        fun toModel (alisOrder: AlisOrder): RmsOrder {
            val labsCheck = alisOrder.organizationId == "G010000"
            val year = alisOrder.birth?.substring(0,2)?.toInt()
            val month = alisOrder.birth?.substring(2,4)?.toByte()
            val day = alisOrder.birth?.substring(4,6)?.toByte()
            val fullYear = if (year != null) {
                if (year in 0..30) { 2000 + year } else { 1900 + year } } else { null }
            val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
            val cost = alisOrder.cost?.minus(alisOrder.tax!!)

            return RmsOrder(
                orderNumber = alisOrder.orderNumber,
                orderId = UUID.randomUUID(),
                itemId = UUID.randomUUID(),
                sampleId = UUID.randomUUID(),
                serviceId = alisOrder.serviceCode,
                createAt = alisOrder.orderDate,
                organizationId = if (labsCheck) alisOrder.organizationSubId else alisOrder.organizationId,
                organizationName = if (labsCheck) alisOrder.labsOrganizationName else alisOrder.organizationName,
                organizationUserId = alisOrder.organizationUserId,
                sampling = alisOrder.sampling ?: alisOrder.orderDate,
                mrn = alisOrder.mrn,
                userId = if (labsCheck) alisOrder.organizationId else alisOrder.organizationUserId,
                state = if (alisOrder.state == true) "CANCEL" else "FINISHED",
                empId = if (labsCheck) alisOrder.labsEmpno else alisOrder.rmsEmpno,
                empName = if (labsCheck) alisOrder.labsEmpnm else alisOrder.rmsEmpnm,
                empMobile = if (labsCheck) alisOrder.labsMobile else alisOrder.rmsMobile,
                serial = if (labsCheck) alisOrder.labsSerial else alisOrder.rmsSerial,
                birthYear = fullYear?.toShort(),
                birthMonth = month,
                birthDay = day,
                genomeBarcode = "${alisOrder.orderDate.format(ofPattern).toInt()}${alisOrder.branchCode}${alisOrder.orderNumber%10000}",
                outsourcingCost = cost,
                price = alisOrder.price,
                type = alisOrder.type,
                sampleTypeId = alisOrder.sampleTypeId,
                sampleBarcode = alisOrder.sampleBarcode,
                patientName = alisOrder.patientName,
                patientAge = alisOrder.patientAge,
                patientSex = alisOrder.sex,
                registrationNumber = alisOrder.registrationNumber,
                nursingNumber = alisOrder.nursingNumber,
                department = alisOrder.department,
                ward = alisOrder.ward,
                physician = alisOrder.physician,
                note = alisOrder.memo,
                branchId = alisOrder.branchCode,
                branchName = alisOrder.branchName,
                genomeBarcodePrefix = alisOrder.orderDate.format(ofPattern).toInt(),
                genomeBarcodeInfix = alisOrder.branchCode!!.toShort(),
                genomeBarcodePostfix = alisOrder.orderNumber%10000,
                userCode = alisOrder.branchCode.toShort()
            )
        }
    }
}






