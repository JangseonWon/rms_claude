package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.PatientRecord
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.data.Patient
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

interface PatientDao{
    fun DSLContext.insertPatient(organizationId: String, userId: String, patient: Patient): Mono<PatientRecord> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.ORGANIZATION_ID, organizationId)
                .set(PATIENT.SERIAL, patient.serial)
                .set(PATIENT.USER_ID, userId)
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .onDuplicateKeyUpdate()
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .returning()
        )
    }

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
