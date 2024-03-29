package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.pojos.Patient
import com.gcgenome.rms.tables.references.PATIENT
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
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
}