package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.data.Patient
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import reactor.core.publisher.Mono

interface PatientDao{
    fun DSLContext.insertPatient(userId: String, patient: Patient) : Mono<Patient> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.SERIAL,patient.serial)
                .set(PATIENT.ORGANIZATION_ID,patient.organization!!.id)
                .set(PATIENT.USER_ID,userId)
                .set(PATIENT.NAME,patient.name)
                .set(PATIENT.SEX,patient.sex)
                .set(PATIENT.BIRTH_YEAR,patient.birthYear)
                .set(PATIENT.BIRTH_MONTH,patient.birthMonth)
                .set(PATIENT.BIRTH_DAY,patient.birthDay)
                .onConflict()
                .doUpdate()
                .set(PATIENT.NAME,patient.name)
                .set(PATIENT.SEX,patient.sex)
                .set(PATIENT.BIRTH_YEAR,patient.birthYear)
                .set(PATIENT.BIRTH_MONTH,patient.birthMonth)
                .set(PATIENT.BIRTH_DAY,patient.birthDay)
                .returning()
        ).map { it.into(Patient::class.java) }
    }
    fun DSLContext.deletePatientById(patient: Patient): Mono<Patient> {
        return Mono.from(
            deleteFrom(PATIENT)
                .where(PATIENT.SERIAL.eq(patient.serial)
                    .and(PATIENT.ORGANIZATION_ID.eq(patient.organizationId))
                    .and(PATIENT.USER_ID.eq(patient.userId))
                    .and(
                        row(PATIENT.SERIAL, PATIENT.ORGANIZATION_ID, PATIENT.USER_ID)
                            .notIn(select(SAMPLE.PATIENT_SERIAL, SAMPLE.ORGANIZATION_ID, SAMPLE.USER_ID)
                                .from(SAMPLE)
                                .where(SAMPLE.PATIENT_SERIAL.eq(patient.serial)
                                    .and(SAMPLE.ORGANIZATION_ID.eq(patient.organizationId))
                                    .and(SAMPLE.USER_ID.eq(patient.userId))
                                )
                            )
                    )
                ).returning()
        ).map { it.into(Patient::class.java) }
    }
}
