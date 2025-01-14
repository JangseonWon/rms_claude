package com.gcgenome.rms.dao

import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface PatientDao {
    fun DSLContext.insertPatient(userId: String, patient: PatientDTO): Mono<PatientDTO> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.SERIAL, patient.serial)
                .set(PATIENT.ORGANIZATION_ID, patient.organization!!.id)
                .set(PATIENT.USER_ID, userId)
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
        ).map { it.into(PatientDTO::class.java) }
    }
    fun DSLContext.deletePatientById(userId: String, patient: PatientDTO): Mono<PatientDTO> {
        return Mono.from(
            deleteFrom(PATIENT)
                .where(PATIENT.SERIAL.eq(patient.serial)
                    .and(PATIENT.ORGANIZATION_ID.eq(patient.organization!!.id))
                    .and(PATIENT.USER_ID.eq(userId))
                    .andNotExists(
                        selectOne()
                            .from(SAMPLE)
                            .where(
                                SAMPLE.PATIENT_SERIAL.eq(patient.serial)
                                    .and(SAMPLE.ORGANIZATION_ID.eq(patient.organization.id))
                                    .and(SAMPLE.USER_ID.eq(userId))
                            )
                    )
                ).returning()
        ).map { it.into(PatientDTO::class.java) }
    }
}