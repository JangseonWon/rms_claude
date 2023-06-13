package com.gcgenome.rms.order

import com.gcgenome.lims.tables.records.PatientRecord
import com.gcgenome.lims.tables.references.PATIENT
import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Patient_
import com.gcgenome.rms.entity.Patient
import com.gcgenome.rms.entity.QPatient.patient
import com.gcgenome.rms.repo.PatientRepository
import org.jooq.Configuration
import org.jooq.InsertResultStep
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.PatientDao")
class PatientDao(
    val patientRepo: PatientRepository
) {
    fun insertPatient(trx: Configuration, userId: String, patient: Patient_): InsertResultStep<PatientRecord> = trx.dsl()
        .insertInto(PATIENT)
        .columns(PATIENT.ORGANIZATION_ID, PATIENT.SERIAL, PATIENT.USER_ID, PATIENT.BIRTH_DAY, PATIENT.BIRTH_MONTH, PATIENT.BIRTH_YEAR, PATIENT.NAME, PATIENT.SEX)
        .values(userId, patient.serial, userId, patient.birthDay?.toByte(), patient.birthMonth?.toByte(), patient.birthYear?.toShort(), patient.name, patient.sex)
        .onDuplicateKeyUpdate()
        .set(PATIENT.BIRTH_DAY, patient.birthDay?.toByte())
        .set(PATIENT.BIRTH_MONTH, patient.birthMonth?.toByte())
        .set(PATIENT.BIRTH_YEAR, patient.birthYear?.toShort())
        .returning()


    fun deletePatient(sampleId: UUID, itemId: UUID, orderId: UUID, mrn: String): Mono<CancelOrder_> =
        patientRepo.deleteBySerial(mrn)
            .then(Mono.just(CancelOrder_(sampleId, "의뢰 취소 되었습니다.")
            .apply {this.itemId=itemId; this.orderId=orderId}))
}
