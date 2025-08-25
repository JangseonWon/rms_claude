package com.gcgenome.rms.dao

import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.patch.PatientPatchDTO
import com.gcgenome.rms.tables.references.PATIENT
import org.jooq.DSLContext
import org.jooq.Field
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

interface PatientDao {
    fun DSLContext.insertPatient(patient: PatientDTO): Mono<PatientDTO> {
        return Mono.from(
            insertInto(PATIENT)
                .set(PATIENT.ID, patient.id)
                .set(PATIENT.SERIAL, patient.serial)
                .set(PATIENT.NAME, patient.name)
                .set(PATIENT.SEX, patient.sex)
                .set(PATIENT.BIRTH, patient.birth)
                .returning()
        ).map { it.into(PatientDTO::class.java) }
    }
    fun DSLContext.deletePatientById(id: UUID): Mono<PatientDTO> {
        return Mono.from(
            deleteFrom(PATIENT).where(PATIENT.ID.eq(id))
                .returning()
        ).map { it.into(PatientDTO::class.java) }
    }

    fun DSLContext.updatePatientFields(
        patientId: UUID,
        patch: PatientPatchDTO
    ): Mono<Boolean> {
        val updates = mutableMapOf<Field<*>, Any?>()
        if (patch.name.isPresent)  updates[PATIENT.NAME]  = patch.name.orElse(null)
        if (patch.sex.isPresent)   updates[PATIENT.SEX]   = patch.sex.orElse(null)
        if (patch.birth.isPresent) updates[PATIENT.BIRTH] = patch.birth.orElse(null)?.let(LocalDate::parse)

        if (updates.isEmpty()) return Mono.just(false)
        return Mono.from(update(PATIENT).set(updates).where(PATIENT.ID.eq(patientId))).thenReturn(true)
    }
}