package com.gcgenome.rms.dao

import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.tables.references.ALIS_ORDER_MV
import com.gcgenome.rms.tables.references.ITEM
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface SampleDao {

    fun DSLContext.insertSample(rmsOrder: RmsOrder): Mono<Sample> {
        return Mono.from(
            insertInto(SAMPLE)
                .set(SAMPLE.ID, rmsOrder.sampleId)
                .set(SAMPLE.CREATE_AT, rmsOrder.createAt)
                .set(SAMPLE.LAST_MODIFY_AT, rmsOrder.createAt)
                .set(SAMPLE.GENOME_BARCODE_PREFIX, rmsOrder.genomeBarcodePrefix)
                .set(SAMPLE.GENOME_BARCODE_INFIX, rmsOrder.genomeBarcodeInfix)
                .set(SAMPLE.GENOME_BARCODE_POSTFIX, rmsOrder.genomeBarcodePostfix)
                .set(SAMPLE.GENOME_BARCODE, rmsOrder.genomeBarcode)
                .set(SAMPLE.SAMPLE_BARCODE, rmsOrder.sampleBarcode)
                .set(SAMPLE.ORGANIZATION_ID, rmsOrder.organizationId)
                .set(SAMPLE.PATIENT_SERIAL, rmsOrder.mrn)
                .set(SAMPLE.USER_ID, rmsOrder.userId)
                .set(SAMPLE.SAMPLE_TYPE_ID, rmsOrder.sampleTypeId)
                .set(SAMPLE.AGE, rmsOrder.patientAge)
                .set(SAMPLE.DEPARTMENT, rmsOrder.department)
                .set(SAMPLE.NOTE, rmsOrder.note)
                .set(SAMPLE.REGISTRATION_AT, rmsOrder.createAt)
                .set(SAMPLE.SAMPLING, rmsOrder.sampling)
                .set(SAMPLE.STATE, rmsOrder.state)
                .set(SAMPLE.WARD, rmsOrder.ward)
                .set(SAMPLE.PHYSICIAN, rmsOrder.physician)
                .set(SAMPLE.ITEM_ID, rmsOrder.itemId)
                .set(SAMPLE.EMP_ID, rmsOrder.empId)
                .set(SAMPLE.EMP_NAME, rmsOrder.empName)
                .set(SAMPLE.EMP_MOBILE, rmsOrder.empMobile)
                .returning()
        ).map { it.into(Sample::class.java) }
    }

    fun DSLContext.checkSampleByServiceId(genomeBarcode: String, serviceId: String): Mono<Sample> {
        return Mono.from(
            select(SAMPLE)
                .from(SAMPLE)
                .join(ITEM).on(SAMPLE.ITEM_ID.eq(ITEM.ID))
                .where(SAMPLE.GENOME_BARCODE.eq(genomeBarcode).and(ITEM.SERVICE_ID.eq(serviceId)))
        ).map { it.into(Sample::class.java) }
    }

    fun DSLContext.updateSampleByGenomeBarcode(sampleId: UUID): Mono<Sample> {
        return Mono.from(
            update(SAMPLE)
                .set(SAMPLE.STATE, "REPORTED")
                .where(SAMPLE.ID.eq(sampleId))
                .returning()
        ).map { it.into(Sample::class.java) }
    }

    fun DSLContext.searchSampleReportByFileServer(): Flux<Sample> {
        return Flux.from(
            select(SAMPLE).from(SAMPLE)
                .join(ITEM).on(SAMPLE.ITEM_ID.eq(ITEM.ID))
                .orderBy(SAMPLE.CREATE_AT.asc(), SAMPLE.GENOME_BARCODE.asc(), ITEM.SERVICE_ID.asc())
        ).map { it.into(Sample::class.java) }
    }
}