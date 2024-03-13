package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.records.SampleRecord
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface SampleDao {
    fun DSLContext.selectSampleById(sampleId: UUID): Flux<SampleRecord> =
        Flux.from(selectFrom(SAMPLE).where(SAMPLE.ID.eq(sampleId)))

    fun DSLContext.selectSampleBarcode(barcodePrefix: String): Mono<String?> {
        return Mono.from(select(SAMPLE.BARCODE)
            .from(SAMPLE)
            .where(SAMPLE.BARCODE.like("$barcodePrefix%"))
            .orderBy(SAMPLE.BARCODE.desc())
            .limit(1))
            .mapNotNull { result ->
                result?.component1()
            }
    }


    fun DSLContext.insertSample(userId: String, orderId: UUID, serviceId: String, sample: Sample, patientSerial: String,
                                organizationId: String?, barcode: String, status: String): Mono<SampleRecord> {
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, UUID.randomUUID())
                .set(SAMPLE.STATUS, status)
                .set(SAMPLE.BARCODE, barcode)
                .set(SAMPLE.SERIAL, sample.serial)
                .set(SAMPLE.QUANTITY, sample.quantity)
                .set(SAMPLE.AGE, sample.age)
                .set(SAMPLE.RETEST_REASON, sample.retestReason)
                .set(SAMPLE.MEMO, sample.ward)
                .set(SAMPLE.DEPARTMENT, sample.department)
                .set(SAMPLE.WARD, sample.ward)
                .set(SAMPLE.PHYSICIAN, sample.physician)
                .set(SAMPLE.SAMPLING, sample.sampling)
                .set(SAMPLE.CREATE_AT, sample.createAt)
                .set(SAMPLE.CART_AT, sample.cartAt)
                .set(SAMPLE.LAST_MODIFY_AT, LocalDateTime.now())
                .set(SAMPLE.EMP_ID, sample.empId)
                .set(SAMPLE.EMP_NAME, sample.empName)
                .set(SAMPLE.EMP_MOBILE, sample.empMobile)
                .set(SAMPLE.LABS_TEST, sample.labsTest)
                .set(SAMPLE.LABS_CREDIT, sample.labsCredit)
                .set(SAMPLE.LABS_PRICE, sample.labsPrice)
                .set(SAMPLE.LABS_OUTSOURCING_COST, sample.labsOutsourcingCost)
                .set(SAMPLE.ORDER_ID, orderId)
                .set(SAMPLE.SERVICE_ID, serviceId)
                .set(SAMPLE.SAMPLE_TYPE_ID, sample.sampleTypeId)
                .set(SAMPLE.PATIENT_SERIAL, patientSerial)
                .set(SAMPLE.ORGANIZATION_ID, organizationId ?: userId)
                .set(SAMPLE.USER_ID, userId)
                .returning()
        )
    }
}