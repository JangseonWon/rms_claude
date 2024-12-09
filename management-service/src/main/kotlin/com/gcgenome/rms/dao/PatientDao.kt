package com.gcgenome.rms.dao

import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Mono

interface PatientDao {
    fun DSLContext.deletePatientById(patient: PatientDTO): Mono<PatientDTO> {
        return Mono.from(
            deleteFrom(PATIENT)
                .where(PATIENT.SERIAL.eq(patient.serial)
                    .and(PATIENT.ORGANIZATION_ID.eq(patient.organization!!.id))
                    .and(PATIENT.USER_ID.eq(patient.organization.userId))
                    .andNotExists(
                        selectOne()
                            .from(SAMPLE)
                            .where(
                                SAMPLE.PATIENT_SERIAL.eq(patient.serial)
                                    .and(SAMPLE.ORGANIZATION_ID.eq(patient.organization.id))
                                    .and(SAMPLE.USER_ID.eq(patient.organization.userId))
                            )
                    )
                ).returning()
        ).map { it.into(PatientDTO::class.java) }
    }
}