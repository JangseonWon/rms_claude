package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.data.Patient
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import reactor.core.publisher.Mono

interface PatientDao{
    fun DSLContext.insertPatient(organizationId: String, userId: String, patient: Patient): Mono<Patient> {
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
        ).map { it.into(Patient::class.java) }
    }

    fun DSLContext.deletePatientById(serial: String, organizationId: String, userId: String): Mono<Patient> {
        return Mono.from(
            deleteFrom(PATIENT)
                .where(PATIENT.SERIAL.eq(serial)
                    .and(PATIENT.ORGANIZATION_ID.eq(organizationId))
                    .and(PATIENT.USER_ID.eq(userId))
                    .and(
                        row(PATIENT.SERIAL, PATIENT.ORGANIZATION_ID, PATIENT.USER_ID)
                            .notIn(select(SAMPLE.PATIENT_SERIAL, SAMPLE.ORGANIZATION_ID, SAMPLE.USER_ID)
                                .from(SAMPLE)
                                .where(SAMPLE.PATIENT_SERIAL.eq(serial)
                                    .and(SAMPLE.ORGANIZATION_ID.eq(organizationId))
                                    .and(SAMPLE.USER_ID.eq(userId))
                                )
                            )
                    )
                ).returning()
        ).map { it.into(Patient::class.java) }
    }
}
