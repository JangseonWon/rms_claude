package com.gcgenome.rms.request.dto.mapper

import com.gcgenome.rms.entity.PatientEntity
import com.gcgenome.rms.entity.RequestEntity
import com.gcgenome.rms.entity.SampleEntity
import com.gcgenome.rms.request.dto.request.RequestPutDTO
import java.time.LocalDateTime
import java.util.*

fun RequestPutDTO.toRequestEntity(
    serviceId: UUID,
    sampleId: UUID,
    organizationId: UUID,
    patientId: UUID
): RequestEntity {
    return RequestEntity(
        id = UUID.randomUUID(),
        department = this.department,
        ward = this.ward,
        physician = this.physician,
        memo = this.memo,
        genomePrice = this.genomePrice,
        labsPrice = this.labsPrice,
        createAt = LocalDateTime.now(),
        isEditable = true,
        isDeletable = true,
        serviceId = serviceId,
        sampleId = sampleId,
        organizationId = organizationId,
        patientId = patientId ,
    )
}

fun RequestPutDTO.toPatientEntity(): PatientEntity {
    return PatientEntity(
        id = UUID.randomUUID(),
        serial = this.patient.serial,
        name = this.patient.name,
        sex = this.patient.sex,
        age = this.patient.age,
        birth = this.patient.birth,
    )
}
fun RequestPutDTO.toSampleEntity(serial: String, sampleTypeId: UUID, userId: UUID): SampleEntity {
    return SampleEntity(
        id = UUID.randomUUID(),
        serial = serial,
        count = this.sample.count,
        samplingOn = this.sample.samplingOn,
        createAt = LocalDateTime.now(),
        sampleTypeId = sampleTypeId,
        userId = userId
    )
}