package com.gcgenome.rms.dao

import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.tables.references.PATIENT
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface PatientDao{
    fun DSLContext.insertPatient(userId: String, patient: PatientDTO): Mono<PatientDTO> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.ORGANIZATION_ID, patient.organization!!.id)
                .set(PATIENT.SERIAL, patient.serial)
                .set(PATIENT.USER_ID, userId)
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .onDuplicateKeyUpdate()
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .set(PATIENT.BIRTH_DAY, patient.birthDay)
                .set(PATIENT.BIRTH_MONTH, patient.birthMonth)
                .set(PATIENT.BIRTH_YEAR, patient.birthYear)
                .returning()
        ).map { it.into(PatientDTO::class.java) }
    }
}
