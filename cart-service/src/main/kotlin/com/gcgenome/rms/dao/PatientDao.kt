package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Patient
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface PatientDao {
    fun DSLContext.insertPatient(patient: Patient): Mono<Patient> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.SERIAL, patient.serial)
                .set(PATIENT.ORGANIZATION_ID, patient.organization!!.id)
                .set(PATIENT.USER_ID, patient.organization!!.user!!.id)
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .onDuplicateKeyUpdate()
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .returning()
        ).map { it.into(Patient::class.java) }
    }
    fun DSLContext.deletePatientById(patient: Patient): Mono<Patient> {
        return Mono.from(
            deleteFrom(PATIENT)
                .where(PATIENT.SERIAL.eq(patient.serial)
                    .and(PATIENT.ORGANIZATION_ID.eq(patient.organization!!.id))
                    .and(PATIENT.USER_ID.eq(patient.organization!!.user!!.id))
                    .andNotExists(
                        selectOne()
                            .from(SAMPLE)
                            .where(
                                SAMPLE.PATIENT_SERIAL.eq(patient.serial)
                                    .and(SAMPLE.ORGANIZATION_ID.eq(patient.organization!!.id))
                                    .and(SAMPLE.USER_ID.eq(patient.organization!!.user!!.id))
                            )
                    )
                ).returning()
        ).map { it.into(Patient::class.java) }
    }
}