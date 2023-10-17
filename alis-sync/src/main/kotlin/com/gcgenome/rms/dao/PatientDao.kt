package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Patient
import com.gcgenome.rms.data.RmsOrder
import com.gcgenome.rms.tables.references.PATIENT
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface PatientDao{
    fun DSLContext.insertPatient(rmsOrder: RmsOrder): Mono<Patient> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.ORGANIZATION_ID, rmsOrder.organizationId)
                .set(PATIENT.SERIAL, rmsOrder.mrn)
                .set(PATIENT.USER_ID, rmsOrder.userId)
                .set(PATIENT.BIRTH_DAY, rmsOrder.birthDay)
                .set(PATIENT.BIRTH_MONTH, rmsOrder.birthMonth)
                .set(PATIENT.BIRTH_YEAR, rmsOrder.birthYear)
                .set(PATIENT.NAME, rmsOrder.patientName)
                .set(PATIENT.SEX, rmsOrder.patientSex)
                .onDuplicateKeyUpdate()
                .set(PATIENT.BIRTH_DAY, rmsOrder.birthDay)
                .set(PATIENT.BIRTH_MONTH, rmsOrder.birthMonth)
                .set(PATIENT.BIRTH_YEAR, rmsOrder.birthYear)
                .set(PATIENT.NAME, rmsOrder.patientName)
                .set(PATIENT.SEX, rmsOrder.patientSex)
                .returning()
        ).map { it.into(Patient::class.java) }
    }

}
