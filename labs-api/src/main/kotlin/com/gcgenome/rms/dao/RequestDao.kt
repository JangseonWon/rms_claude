package com.gcgenome.rms.dao

import com.gcgenome.rms.entity.RequestEntity
import com.gcgenome.rms.organization.dto.response.OrganizationResponseDTO
import com.gcgenome.rms.request.dto.request.RequestPatchDTO
import com.gcgenome.rms.request.dto.request.RequestPostDTO
import com.gcgenome.rms.request.dto.response.*
import com.gcgenome.rms.service.dto.response.ServiceResponseDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.*
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface RequestDao {
    private fun extensionsFieldForRequest(): Field<List<RequestExtensionDTO>> =
        multiset(
            select(
                REQUEST_EXTENSION.ID.`as`("id"),
                EXTENSION.CODE.`as`("code"),
                REQUEST_EXTENSION.VALUE.`as`("value"),
                EXTENSION.NAME_KR.`as`("nameKr"),
                EXTENSION.NAME_EN.`as`("nameEn"),
                SERVICE_EXTENSION.IS_REQUIRED.`as`("isRequired"),
                REQUEST_EXTENSION.EXTENSION_ID.`as`("extensionId"),
                REQUEST_EXTENSION.REQUEST_ID.`as`("requestId")
            )
                .from(REQUEST_EXTENSION)
                .join(EXTENSION).on(EXTENSION.ID.eq(REQUEST_EXTENSION.EXTENSION_ID))
                .join(SERVICE_EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                .where(REQUEST_EXTENSION.REQUEST_ID.eq(REQUEST.ID))
        ).convertFrom { r: Result<Record8<UUID?, String?, String?, String?, String?, Boolean?, UUID?, UUID?>> ->
            r.map { it.into(RequestExtensionDTO::class.java) }
        }
    fun DSLContext.selectRequestByServiceIdAndSampleId(serviceId: UUID, sampleId: UUID): Mono<RequestResponseDTO> {
        val extensionsField = extensionsFieldForRequest()

        return Mono.from(
            select(
                REQUEST.asterisk(),
                PATIENT.asterisk(),
                ORGANIZATION.asterisk(),
                SERVICE.asterisk(),
                USER_SERVICE.SERIAL,
                SAMPLE.asterisk(),
                SAMPLE_TYPE.CODE,
                SAMPLE_TYPE.NAME_KR,
                SAMPLE_TYPE.NAME_EN,
                USER_SAMPLE_TYPE.SERIAL,
                SAMPLE_TYPE.ID,
                extensionsField
            ).from(REQUEST)
                .join(PATIENT).on(PATIENT.ID.eq(REQUEST.PATIENT_ID))
                .join(ORGANIZATION).on(ORGANIZATION.ID.eq(REQUEST.ORGANIZATION_ID))
                .join(SERVICE).on(SERVICE.ID.eq(REQUEST.SERVICE_ID))
                .join(SAMPLE).on(SAMPLE.ID.eq(REQUEST.SAMPLE_ID))
                .join(USER_SERVICE).on(USER_SERVICE.SERVICE_ID.eq(SERVICE.ID).and(USER_SERVICE.USER_ID.eq(SAMPLE.USER_ID)))
                .join(SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SAMPLE.SAMPLE_TYPE_ID))
                .join(USER_SAMPLE_TYPE).on(USER_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
                .where(
                    REQUEST.SERVICE_ID.eq(serviceId),
                    REQUEST.SAMPLE_ID.eq(sampleId)
                )
        ).map { rec ->
            val request = rec.into(REQUEST).into(RequestResponseDTO::class.java)
            val patient = rec.into(PATIENT).into(PatientResponseDTO::class.java)
            val organization = rec.into(ORGANIZATION).into(OrganizationResponseDTO::class.java)
            val service = rec.into(SERVICE).into(ServiceResponseDTO::class.java)
            val sample = rec.into(SAMPLE).into(RequestSampleDTO::class.java)
            val extList = rec.get(extensionsField)

            service.serial = rec.get(USER_SERVICE.SERIAL).toString()
            sample.type = SampleTypeResponseDTO(
                code = rec.get(SAMPLE_TYPE.CODE).toString(),
                serial = rec.get(USER_SAMPLE_TYPE.SERIAL).toString(),
                nameKr = rec.get(SAMPLE_TYPE.NAME_KR),
                nameEn = rec.get(SAMPLE_TYPE.NAME_EN),
                id = rec.get(SAMPLE_TYPE.ID)
            )

            request.apply {
                this.patient = patient
                this.organization = organization
                this.service = service
                this.sample = sample
                this.extensions = extList
            }
        }
    }
    fun DSLContext.selectRequestById(id: UUID): Mono<RequestResponseDTO> {
        val extensionsField = extensionsFieldForRequest()
        return Mono.from(
            select(
                REQUEST.asterisk(),
                PATIENT.asterisk(),
                ORGANIZATION.asterisk(),
                SERVICE.asterisk(),
                USER_SERVICE.SERIAL,
                SAMPLE.asterisk(),
                SAMPLE_TYPE.asterisk(),
                USER_SAMPLE_TYPE.SERIAL,
                extensionsField
            ).from(REQUEST)
                .join(PATIENT).on(PATIENT.ID.eq(REQUEST.PATIENT_ID))
                .join(ORGANIZATION).on(ORGANIZATION.ID.eq(REQUEST.ORGANIZATION_ID))
                .join(SERVICE).on(SERVICE.ID.eq(REQUEST.SERVICE_ID))
                .join(SAMPLE).on(SAMPLE.ID.eq(REQUEST.SAMPLE_ID))
                .join(USER_SERVICE).on(USER_SERVICE.SERVICE_ID.eq(SERVICE.ID).and(USER_SERVICE.USER_ID.eq(SAMPLE.USER_ID)))
                .join(SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SAMPLE.SAMPLE_TYPE_ID))
                .join(USER_SAMPLE_TYPE).on(USER_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
                .where(REQUEST.ID.eq(id))
        ).map { rec ->
            val request = rec.into(REQUEST).into(RequestResponseDTO::class.java)

            val patient = rec.into(PATIENT).into(PatientResponseDTO::class.java)
            val organization = rec.into(ORGANIZATION).into(OrganizationResponseDTO::class.java)
            val service = rec.into(SERVICE).into(ServiceResponseDTO::class.java)
            val sample = rec.into(SAMPLE).into(RequestSampleDTO::class.java)
            val extList = rec.get(extensionsField)

            service.serial = rec.get(USER_SERVICE.SERIAL).toString()
            sample.type = SampleTypeResponseDTO(
                code = rec.get(SAMPLE_TYPE.CODE).toString(),
                serial = rec.get(USER_SAMPLE_TYPE.SERIAL).toString(),
                nameKr = rec.get(SAMPLE_TYPE.NAME_KR),
                nameEn = rec.get(SAMPLE_TYPE.NAME_EN),
                id = rec.get(SAMPLE_TYPE.ID)
            )

            request.apply {
                this.patient = patient
                this.organization = organization
                this.service = service
                this.sample = sample
                this.extensions = extList
            }
        }
    }
    fun DSLContext.insertRequest(requestEntity: RequestEntity): Mono<Int> {
        return Mono.from(
            insertInto(REQUEST)
                .set(REQUEST.ID, requestEntity.id)
                .set(REQUEST.GENOME_PRICE, requestEntity.genomePrice)
                .set(REQUEST.LABS_PRICE, requestEntity.labsPrice)
                .set(REQUEST.DEPARTMENT, requestEntity.department)
                .set(REQUEST.WARD, requestEntity.ward)
                .set(REQUEST.PHYSICIAN, requestEntity.physician)
                .set(REQUEST.CREATE_AT, LocalDateTime.now())
                .set(REQUEST.SERVICE_ID, requestEntity.serviceId)
                .set(REQUEST.SAMPLE_ID, requestEntity.sampleId)
                .set(REQUEST.ORGANIZATION_ID, requestEntity.organizationId)
                .set(REQUEST.PATIENT_ID, requestEntity.patientId)
                .set(REQUEST.IS_EDITABLE, requestEntity.isEditable)
                .set(REQUEST.IS_DELETABLE, requestEntity.isDeletable)
        )
    }

    fun DSLContext.deleteRequestById(requestId: UUID): Mono<Int> {
        return Mono.from(
            deleteFrom(REQUEST).where(REQUEST.ID.eq(requestId))
        )
    }

    fun DSLContext.searchRequests(
        userId: UUID,
        fromInclusive: LocalDateTime,
        toExclusive: LocalDateTime,
        q: RequestPostDTO
    ): Flux<RequestResponseDTO> {

        val usSerialField = USER_SERVICE.SERIAL
        val extensionsField = extensionsFieldForRequest()

        var cond: Condition = trueCondition()
            .and(REQUEST.CREATE_AT.ge(fromInclusive))
            .and(REQUEST.CREATE_AT.lt(toExclusive))
            .and(SAMPLE.USER_ID.eq(userId))

        q.sample?.serial?.takeIf { it.isBlank().not() }?.let { cond = cond.and(SAMPLE.SERIAL.eq(it)) }
        q.service?.serial?.takeIf { it.isBlank().not() }?.let { cond = cond.and(USER_SERVICE.SERIAL.eq(it)) }
        q.organization?.serial?.let { cond = cond.and(ORGANIZATION.SERIAL.eq(it)) }
        q.patient?.serial?.takeIf { it.isBlank().not() }?.let { cond = cond.and(PATIENT.SERIAL.eq(it)) }
        q.patient?.name?.takeIf { it.isBlank().not() }?.let { cond = cond.and(PATIENT.NAME.likeIgnoreCase("%$it%")) }

        return Flux.from(
            select(
                REQUEST.asterisk(),
                PATIENT.asterisk(),
                ORGANIZATION.asterisk(),
                SERVICE.asterisk(),
                SAMPLE.asterisk(),
                usSerialField,
                extensionsField
            )
                .from(REQUEST)
                .join(PATIENT).on(PATIENT.ID.eq(REQUEST.PATIENT_ID))
                .join(ORGANIZATION).on(ORGANIZATION.ID.eq(REQUEST.ORGANIZATION_ID))
                .join(SERVICE).on(SERVICE.ID.eq(REQUEST.SERVICE_ID))
                .join(SAMPLE).on(SAMPLE.ID.eq(REQUEST.SAMPLE_ID))
                .join(USER_SERVICE).on(USER_SERVICE.SERVICE_ID.eq(SERVICE.ID).and(USER_SERVICE.USER_ID.eq(SAMPLE.USER_ID)))
                .where(cond)
                .orderBy(REQUEST.CREATE_AT.desc())
        ).map { rec ->
            val dto = rec.into(REQUEST).into(RequestResponseDTO::class.java)
            dto.patient = rec.into(PATIENT).into(PatientResponseDTO::class.java)
            dto.organization = rec.into(ORGANIZATION).into(OrganizationResponseDTO::class.java)
            dto.service = rec.into(SERVICE).into(ServiceResponseDTO::class.java).apply {
                serial = rec.get(usSerialField).toString()
            }
            dto.sample = rec.into(SAMPLE).into(RequestSampleDTO::class.java)
            dto.extensions = rec.get(extensionsField)
            dto
        }
    }

    fun DSLContext.updateRequestById(requestId: UUID, patch: RequestPatchDTO): Mono<Boolean> {

        val updates = mutableMapOf<Field<*>, Any?>()

        if (patch.department.isPresent)  updates[REQUEST.DEPARTMENT]   = patch.department.orElse(null)
        if (patch.ward.isPresent)        updates[REQUEST.WARD]         = patch.ward.orElse(null)
        if (patch.physician.isPresent)   updates[REQUEST.PHYSICIAN]    = patch.physician.orElse(null)
        if (patch.memo.isPresent)        updates[REQUEST.MEMO]         = patch.memo.orElse(null)
        if (patch.genomePrice.isPresent) updates[REQUEST.GENOME_PRICE] = patch.genomePrice.orElse(null)
        if (patch.labsPrice.isPresent)   updates[REQUEST.LABS_PRICE]   = patch.labsPrice.orElse(null)

        if (updates.isEmpty()) return Mono.just(false)

        return Mono.from(update(REQUEST).set(updates).where(REQUEST.ID.eq(requestId)))
            .thenReturn(true)
    }

    fun DSLContext.updateRequestOrganizationById(requestId: UUID, organizationId: UUID): Mono<Boolean> {
        return Mono.from(
            update(REQUEST)
                .set(REQUEST.ORGANIZATION_ID, organizationId)
                .where(REQUEST.ID.eq(requestId))
        ).thenReturn(true)
    }
}