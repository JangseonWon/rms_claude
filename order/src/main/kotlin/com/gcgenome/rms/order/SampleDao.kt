package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.SAMPLE
import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Item_
import com.gcgenome.rms.data.Patient_
import com.gcgenome.rms.data.Sample_
import com.gcgenome.rms.entity.Sample
import com.gcgenome.rms.repo.SampleRepository
import org.jooq.Configuration
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Repository("com.gcgenome.rms.order.SampleDao")
class SampleDao(
    val sampleRepo: SampleRepository,
    val extensionDao: ExtensionDao
) {
    fun insertSample(trx: Configuration, patientSerial: String, userId: String, itemId: UUID, sample: Sample_) = trx.dsl()
        .insertInto(SAMPLE)
        .columns(
            SAMPLE.ID,
            SAMPLE.ORGANIZATION_ID,
            SAMPLE.PATIENT_SERIAL,
            SAMPLE.USER_ID,
            SAMPLE.SAMPLE_TYPE_ID,
            SAMPLE.AGE,
            SAMPLE.DEPARTMENT,
            SAMPLE.NOTE,
            SAMPLE.REGISTRATION_AT,
            SAMPLE.SAMPLING,
            SAMPLE.SERIAL,
            SAMPLE.STATE,
            SAMPLE.WARD,
            SAMPLE.PHYSICIAN,
            SAMPLE.ITEM_ID)
        .values(
            UUID.randomUUID(),
            userId,
            patientSerial,
            userId,
            sample.typeId,
            sample.age,
            sample.department,
            sample.note,
            LocalDateTime.now(),
            sample.sampling!!.atStartOfDay(),
            sample.serial,
            "REQUEST",
            sample.ward,
            sample.physician,
            itemId,
        ).returning()
    fun deleteSample(sampleId: UUID): Mono<CancelOrder_> =
        sampleRepo.findById(sampleId)
            .flatMap { sample ->
                if (sample.state == "REPORTED" || sample.state == "COMPLETE") {
                    Mono.just(CancelOrder_(sampleId, "완료 되어 취소가 불가능합니다. 관련 추가 문의는 GC지놈에 연락바랍니다."))
                } else if (sample.state == "REGISTRATION") {
                    Mono.just(CancelOrder_(sampleId, "실험 중으로 취소가 불가능합니다. 관련 추가 문의는 GC지놈에 연락바랍니다."))
                } else {
                    extensionDao.existExtension(sampleId)
                        .flatMap { exists ->
                            val deleteExtensions = if (exists) {
                                extensionDao.deleteExtension(sampleId)
                            } else { Mono.empty() }
                            deleteExtensions
                                .then(sampleRepo.delete(sample))
                                .then(Mono.just(CancelOrder_(sampleId, "의뢰 취소 되었습니다.")))
                        }
                }
            }

    fun countSample(sampleId: UUID): Mono<Long> {
        return sampleRepo.findById(sampleId)
            .flatMap { sampleRepo.countByItemId(it.itemId) }
    }

    fun findSample(sampleId: UUID): Mono<Sample> = sampleRepo.findById(sampleId)
}