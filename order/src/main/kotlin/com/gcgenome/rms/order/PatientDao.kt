package com.gcgenome.rms.order

import com.gcgenome.lims.tables.records.PatientRecord
import com.gcgenome.lims.tables.references.PATIENT
import com.gcgenome.rms.data.Patient
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

interface PatientDao{
    fun DSLContext.insertPatient(organizationId: String, userId: String, patient: Patient): Mono<PatientRecord> =
        Mono.from(
            insertInto(PATIENT)
                .columns(PATIENT.ORGANIZATION_ID, PATIENT.SERIAL, PATIENT.USER_ID, PATIENT.BIRTH_DAY, PATIENT.BIRTH_MONTH, PATIENT.BIRTH_YEAR, PATIENT.NAME, PATIENT.SEX)
                .values(organizationId, patient.serial, userId, patient.birthDay?.toByte(), patient.birthMonth?.toByte(), patient.birthYear?.toShort(), patient.name, patient.sex)
                .onDuplicateKeyUpdate()
                .set(PATIENT.BIRTH_DAY, patient.birthDay?.toByte())
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth?.toByte())
                .set(PATIENT.BIRTH_YEAR, patient.birthYear?.toShort())
                .returning()
        )
    fun DSLContext.updatePatientById(userId:String, patient: Patient) =
        Mono.from(
            update(PATIENT)
                .set(PATIENT.SEX, patient.sex)
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.BIRTH_DAY, patient.birthDay?.toByte())
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth?.toByte())
                .set(PATIENT.BIRTH_YEAR, patient.birthYear?.toShort())
                .where(PATIENT.ORGANIZATION_ID.eq(patient.organization?.id ?: userId)
                    .and(PATIENT.USER_ID.eq(userId).and(PATIENT.SERIAL.eq(patient.serial))))
        )

    fun DSLContext.deletePatient(mrn: String) =
        deleteFrom(PATIENT).where(PATIENT.SERIAL.eq(mrn)).toMono()
}
