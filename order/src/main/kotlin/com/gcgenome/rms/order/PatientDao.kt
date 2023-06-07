package com.gcgenome.rms.order

import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Patient_
import com.gcgenome.rms.entity.Patient
import com.gcgenome.rms.entity.QPatient.patient
import com.gcgenome.rms.repo.PatientRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.PatientDao")
class PatientDao(
    val patientRepo: PatientRepository
) {
    fun findPatient(userId: String, dto: Patient_): Mono<Patient_> {
        return patientRepo.findOne(patient.serial.eq(dto.serial)
            .and(patient.organizationId.eq(userId)
                .and(patient.userId.eq(userId))
            )
        ).map { entity->map(dto, entity) }
    }

    fun savePatient(userId: String, dto: Patient_): Mono<Patient_> = patientRepo.save(map(userId, dto)).map { entity->map(dto, entity) }

    fun deletePatient(sampleId: UUID, itemId: UUID, orderId: UUID, mrn: String): Mono<CancelOrder_> =
        patientRepo.deleteBySerial(mrn)
            .then(Mono.just(CancelOrder_(sampleId, "의뢰 취소 되었습니다.")
            .apply {this.itemId=itemId; this.orderId=orderId}))
    private fun map(userId: String, dto: Patient_) = Patient(
        serial = dto.serial!!,
        organizationId = userId,
        userId = userId,
        sex = dto.sex!!,
        name = dto.name!!,
        birthYear = dto.birthYear,
        birthMonth = dto.birthMonth,
        birthDay = dto.birthDay
    )
    private fun map(dto: Patient_, entity: Patient) = Patient_(
        serial = entity.serial,
        sex = entity.sex,
        name = entity.name,
        birthYear = entity.birthYear,
        birthMonth = entity.birthMonth,
        birthDay = entity.birthDay,
        samples = dto.samples
    ).apply {

    }
}
