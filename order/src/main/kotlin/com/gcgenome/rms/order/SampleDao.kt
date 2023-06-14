package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.SAMPLE
import com.gcgenome.rms.data.Sample_
import org.jooq.Configuration
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository("com.gcgenome.rms.order.SampleDao")
class SampleDao(
    val extensionDao: ExtensionDao
) {
    fun insertSample(trx: Configuration, patientSerial: String, userId: String, itemId: UUID, sample: Sample_) = trx.dsl()
        .insertInto(SAMPLE)
        .columns(
            SAMPLE.ID,
            SAMPLE.ORGANIZATION_ID,
            SAMPLE.PATIENT_SERIAL,
            SAMPLE.USER_ID,
            SAMPLE.SAMPLE_TYPE_ID,
            SAMPLE.AGE,
            SAMPLE.DEPARTMENT,
            SAMPLE.NOTE,
            SAMPLE.REGISTRATION_AT,
            SAMPLE.SAMPLING,
            SAMPLE.SERIAL,
            SAMPLE.STATE,
            SAMPLE.WARD,
            SAMPLE.PHYSICIAN,
            SAMPLE.ITEM_ID)
        .values(
            UUID.randomUUID(),
            userId,
            patientSerial,
            userId,
            sample.typeId,
            sample.age,
            sample.department,
            sample.note,
            LocalDateTime.now(),
            sample.sampling!!.atStartOfDay(),
            sample.serial,
            "REQUEST",
            sample.ward,
            sample.physician,
            itemId,
        ).returning()
}