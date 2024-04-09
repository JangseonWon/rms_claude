package com.gcgenome.rms.dao

import com.gcgenome.rms.data.Patient
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import reactor.core.publisher.Mono
import java.util.*

interface PatientDao {
    fun DSLContext.selectPatient(sampleId: UUID) : Mono<Patient> {
        return Mono.from(
            select(PATIENT)
                .from(PATIENT).join(SAMPLE).on(SAMPLE.ID.eq(sampleId))
                .where(PATIENT.SERIAL.eq(SAMPLE.PATIENT_SERIAL))
        ).map { it.into(Patient::class.java) }
    }

    fun DSLContext.deletePatientById(serial: String): Mono<Patient> {
        return Mono.from(
            deleteFrom(PATIENT)
                .where(PATIENT.SERIAL.eq(serial)
                        .and(row(PATIENT.SERIAL)
                            .notIn(select(SAMPLE.PATIENT_SERIAL)
                                .from(SAMPLE)
                                .where(SAMPLE.PATIENT_SERIAL.eq(serial))
                        ))
                ).returning()
        ).map { it.into(Patient::class.java) }
    }
}