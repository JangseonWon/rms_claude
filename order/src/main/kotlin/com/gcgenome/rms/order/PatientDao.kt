package com.gcgenome.rms.order

import com.gcgenome.lims.tables.records.PatientRecord
import com.gcgenome.lims.tables.references.PATIENT
import com.gcgenome.rms.data.Patient_
import org.jooq.Configuration
import org.jooq.InsertResultStep
import org.springframework.stereotype.Repository
import java.util.*

@Repository("com.gcgenome.rms.order.PatientDao")
class PatientDao{
    fun insertPatient(trx: Configuration, userId: String, patient: Patient_): InsertResultStep<PatientRecord> = trx.dsl()
        .insertInto(PATIENT)
        .columns(PATIENT.ORGANIZATION_ID, PATIENT.SERIAL, PATIENT.USER_ID, PATIENT.BIRTH_DAY, PATIENT.BIRTH_MONTH, PATIENT.BIRTH_YEAR, PATIENT.NAME, PATIENT.SEX)
        .values(userId, patient.serial, userId, patient.birthDay?.toByte(), patient.birthMonth?.toByte(), patient.birthYear?.toShort(), patient.name, patient.sex)
        .onDuplicateKeyUpdate()
        .set(PATIENT.BIRTH_DAY, patient.birthDay?.toByte())
        .set(PATIENT.BIRTH_MONTH, patient.birthMonth?.toByte())
        .set(PATIENT.BIRTH_YEAR, patient.birthYear?.toShort())
        .returning()

}
