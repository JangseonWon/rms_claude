package com.gcgenome.rms.order

import com.gcgenome.rms.data.Item_
import com.gcgenome.rms.data.Patient_
import com.gcgenome.rms.data.Sample_
import com.gcgenome.rms.entity.Sample
import com.gcgenome.rms.repo.SampleRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.SampleDao")
class SampleDao(
    val sampleRepo: SampleRepository
) {
    fun saveSample(userId: String, item: Item_, patientDto: Patient_, sampleDto: Sample_): Mono<Sample_> = sampleRepo.save(map(userId, item, patientDto, sampleDto)).map { entity->map(sampleDto, entity) }

    fun map(userId: String, item: Item_, patientDto: Patient_, sampleDto: Sample_) = Sample(
        _id = UUID.randomUUID(),
        sampleTypeId = sampleDto.typeId!!,
        patientSerial = patientDto.serial!!,
        organizationId = userId,
        userId = userId,
        serial = sampleDto.serial,
        age = sampleDto.age,
        sampling = sampleDto.sampling!!,
        note = sampleDto.note,
        state = sampleDto.state,
        department = sampleDto.department,
        ward = sampleDto.ward,
        physician = sampleDto.physician,
        itemId = item.id!!
    )
    private fun map(sampleDto: Sample_, entity: Sample) = Sample_(
        typeId = entity.sampleTypeId,
        registrationAt = entity.registrationAt.toString(),
        organizationId = entity. organizationId,
        serial = entity.serial,
        age = entity.age,
        sampling = entity.sampling,
        note = entity.note,
        department = entity.department,
        ward = entity.ward,
        physician = entity.physician,
        extensions = sampleDto.extensions
    ).apply {
        id = entity._id
    }
}