package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Patient
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Mono
import java.util.*

interface PatientDao {
    fun DSLContext.selectPatientBySampleId(sampleId: UUID?) : Mono<Patient> {
        return Mono.from(
            select(PATIENT)
                .from(PATIENT).join(SAMPLE).on(SAMPLE.ID.eq(sampleId))
                .where(PATIENT.SERIAL.eq(SAMPLE.PATIENT_SERIAL))
        ).map { it.into(Patient::class.java) }
    }

    fun DSLContext.insertPatient(patient: Patient, userId: String?): Mono<Patient> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.SERIAL, patient.serial)
                .set(PATIENT.ORGANIZATION_ID, patient.organization?.id)
                .set(PATIENT.USER_ID, userId)
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .onDuplicateKeyUpdate()
                .set(PATIENT.NAME, coalesce(`val`(patient.name), PATIENT.NAME))
                .set(PATIENT.SEX, coalesce(`val`(patient.sex), PATIENT.SEX))
                .set(PATIENT.BIRTH_DAY, coalesce(`val`(patient.birthDay), PATIENT.BIRTH_DAY))
                .set(PATIENT.BIRTH_MONTH, coalesce(`val`(patient.birthMonth), PATIENT.BIRTH_MONTH))
                .set(PATIENT.BIRTH_YEAR, coalesce(`val`(patient.birthYear), PATIENT.BIRTH_YEAR))
                .set(PATIENT.ORGANIZATION_ID, coalesce(`val`(patient.organization!!.id), PATIENT.ORGANIZATION_ID))
                .where(PATIENT.SERIAL.eq(patient.serial).and(PATIENT.ORGANIZATION_ID.eq(patient.organization.id).and(
                    PATIENT.USER_ID.eq(userId))))
                .returning()
        ).map { it.into(Patient::class.java) }
    }

    fun DSLContext.deletePatientById(patient: Patient, userId: String?): Mono<Patient> {
        return Mono.from(
            deleteFrom(PATIENT)
                .where(PATIENT.SERIAL.notIn(
                    select(PATIENT.SERIAL)
                        .from(PATIENT)
                        .join(SAMPLE).on(PATIENT.SERIAL.eq(SAMPLE.PATIENT_SERIAL))
                )).returning()
        ).map { it.into(Patient::class.java) }
    }
}