package com.gcgenome.rms.dao

import com.gcgenome.rms.data.*
import com.gcgenome.rms.data.patch.RequestPatchDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record4
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.trueCondition
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

interface RequestDao {
    fun DSLContext.selectRequestByServiceIdAndSampleId(serviceId: UUID, sampleId: UUID): Mono<RequestDTO> {
        val extensionsField =
            multiset(
                select(
                    EXTENSION.NAME_KR.`as`("nameKr"),
                    EXTENSION.NAME_EN.`as`("nameEn"),
                    EXTENSION.CODE.`as`("code"),
                    REQUEST_EXTENSION.VALUE.`as`("value")
                ).from(REQUEST_EXTENSION)
                    .join(EXTENSION).on(EXTENSION.ID.eq(REQUEST_EXTENSION.EXTENSION_ID))
                    .where(REQUEST_EXTENSION.REQUEST_ID.eq(REQUEST.ID))
            ).convertFrom { r: org.jooq.Result<Record4<String?, String?, String?, String?>> ->
                r.map { it.into(RequestExtensionDTO::class.java) }
            }
        return Mono.from(
            select(
                REQUEST.asterisk(),
                PATIENT.asterisk(),
                ORGANIZATION.asterisk(),
                SERVICE.asterisk(),
                USER_SERVICE.SERIAL,
                SAMPLE.asterisk(),
                SAMPLE_TYPE.CODE,
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
                .where(
                    REQUEST.SERVICE_ID.eq(serviceId),
                    REQUEST.SAMPLE_ID.eq(sampleId)
                )
        ).map { rec ->
            val request = rec.into(REQUEST).into(RequestDTO::class.java)
            val patient = rec.into(PATIENT).into(PatientDTO::class.java)
            val organization = rec.into(ORGANIZATION).into(OrganizationDTO::class.java)
            val service = rec.into(SERVICE).into(ServiceDTO::class.java)
            val sample = rec.into(SAMPLE).into(SampleDTO::class.java)
            val extension = rec.get(extensionsField)

            service.serial = rec.get(USER_SERVICE.SERIAL)
            sample.type = SampleTypeDTO(code = rec.get(SAMPLE_TYPE.CODE), serial = rec.get(USER_SAMPLE_TYPE.SERIAL))
            request.patient = patient
            request.organization = organization
            request.service = service
            request.sample = sample
            request.extensions = extension

            request
        }
    }
    fun DSLContext.selectRequestById(id: UUID): Mono<RequestDTO> {
        val extensionsField =
            multiset(
                select(
                    EXTENSION.NAME_KR.`as`("nameKr"),
                    EXTENSION.NAME_EN.`as`("nameEn"),
                    EXTENSION.CODE.`as`("code"),
                    REQUEST_EXTENSION.VALUE.`as`("value")
                ).from(REQUEST_EXTENSION)
                    .join(EXTENSION).on(EXTENSION.ID.eq(REQUEST_EXTENSION.EXTENSION_ID))
                    .where(REQUEST_EXTENSION.REQUEST_ID.eq(REQUEST.ID))
            ).convertFrom { r: org.jooq.Result<Record4<String?, String?, String?, String?>> ->
                r.map { it.into(RequestExtensionDTO::class.java) }
            }
        return Mono.from(
            select(
                REQUEST.asterisk(),
                PATIENT.asterisk(),
                ORGANIZATION.asterisk(),
                SERVICE.asterisk(),
                USER_SERVICE.SERIAL,
                SAMPLE.asterisk(),
                SAMPLE_TYPE.CODE,
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
            val request = rec.into(REQUEST).into(RequestDTO::class.java)
            val patient = rec.into(PATIENT).into(PatientDTO::class.java)
            val organization = rec.into(ORGANIZATION).into(OrganizationDTO::class.java)
            val service = rec.into(SERVICE).into(ServiceDTO::class.java).apply { serial = rec.get(USER_SERVICE.SERIAL) }
            val sample = rec.into(SAMPLE).into(SampleDTO::class.java).apply { type = SampleTypeDTO(code = rec.get(SAMPLE_TYPE.CODE), serial = rec.get(USER_SAMPLE_TYPE.SERIAL)) }
            val extension = rec.get(extensionsField)

            request.patient = patient
            request.organization = organization
            request.service = service
            request.sample = sample
            request.extensions = extension

            request
        }
    }
    fun DSLContext.insertRequest(request: RequestDTO): Mono<RequestDTO> {
        return Mono.from(
            insertInto(REQUEST)
                .set(REQUEST.ID, request.id)
                .set(REQUEST.GENOME_PRICE, request.genomePrice)
                .set(REQUEST.LABS_PRICE, request.labsPrice)
                .set(REQUEST.DEPARTMENT, request.department)
                .set(REQUEST.WARD, request.ward)
                .set(REQUEST.PHYSICIAN, request.physician)
                .set(REQUEST.CREATE_AT, LocalDateTime.now())
                .set(REQUEST.SERVICE_ID, request.serviceId)
                .set(REQUEST.SAMPLE_ID, request.sampleId)
                .set(REQUEST.ORGANIZATION_ID, request.organizationId)
                .set(REQUEST.PATIENT_ID, request.patientId)
                .set(REQUEST.IS_EDITABLE, true)
                .set(REQUEST.IS_DELETABLE, true)
                .returning()
        ).map { it.into(RequestDTO::class.java) }
    }

    fun DSLContext.deleteRequestById(requestId: UUID): Mono<RequestDTO> {
        return Mono.from(
            deleteFrom(REQUEST).where(REQUEST.ID.eq(requestId))
                .returning()
        ).map { it.into(RequestDTO::class.java) }
    }

    fun DSLContext.searchRequests(
        userId: UUID,
        fromInclusive: LocalDateTime,
        toExclusive: LocalDateTime,
        q: RequestSearchDTO
    ): Flux<RequestDTO> {

        val usSerialField = USER_SERVICE.SERIAL

        val extensionsField =
            multiset(
                select(
                    EXTENSION.NAME_KR.`as`("nameKr"),
                    EXTENSION.NAME_EN.`as`("nameEn"),
                    EXTENSION.CODE.`as`("code"),
                    REQUEST_EXTENSION.VALUE.`as`("value")
                )
                    .from(REQUEST_EXTENSION)
                    .join(EXTENSION).on(EXTENSION.ID.eq(REQUEST_EXTENSION.EXTENSION_ID))
                    .where(REQUEST_EXTENSION.REQUEST_ID.eq(REQUEST.ID))
            ).convertFrom { r: org.jooq.Result<Record4<String?, String?, String?, String?>> ->
                r.map { it.into(RequestExtensionDTO::class.java) }
            }

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
            val dto = rec.into(REQUEST).into(RequestDTO::class.java)
            dto.patient = rec.into(PATIENT).into(PatientDTO::class.java)
            dto.organization = rec.into(ORGANIZATION).into(OrganizationDTO::class.java)
            dto.service = rec.into(SERVICE).into(ServiceDTO::class.java).apply {
                serial = rec.get(usSerialField)
            }
            dto.sample = rec.into(SAMPLE).into(SampleDTO::class.java)
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