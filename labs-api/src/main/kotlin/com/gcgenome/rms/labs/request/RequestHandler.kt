package com.gcgenome.rms.labs.request

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.data.patch.RequestPatchDTO
import com.gcgenome.rms.exception.*
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class RequestHandler(
    private val dsl: DSLContext
): PatientDao, RequestDao, UserServiceDao, OrganizationDao, UserSampleTypeDao, SampleDao, ServiceExtensionDao, RequestExtensionDao, ExtensionDao  {

    fun searchRequests(userId: UUID, q: RequestSearchDTO): Flux<RequestDTO> {
        val from = q.requestDateFrom
        val to   = q.requestDateTo
        val errors = mutableListOf<FieldError>()
        if (from == null) errors += FieldError("request_data_from", "required")
        if (to == null)   errors += FieldError("request_data_to", "required")
        if (from != null && to != null) {
            if (from.isAfter(to)) errors += FieldError("request_data_from", "must be <= request_data_to", from)
            val days = ChronoUnit.DAYS.between(from, to)
            if (days > 31) errors += FieldError("request_data_to", "range must be <= 31 days", to)
        }
        if (errors.isNotEmpty()) return Flux.error(UnprocessableEntityException(errors))

        val startTs = from!!.atStartOfDay()
        val endTsExclusive = to!!.plusDays(1).atStartOfDay()

        return dsl.searchRequests(userId, startTs, endTsExclusive, q)
    }

    fun getRequestBySerial(userId: UUID, serviceSerial: String, sampleSerial: String): Mono<RequestDTO> {
        return Mono.from(dsl.transactionPublisher { trx ->
            val trxDsl = trx.dsl()
            Mono.zip(
                trxDsl.selectUserServiceByUserIdAndSerial(userId, serviceSerial).orUnprocessable(listOf(FieldError(field = "service.serial", message = "not found"))),
                trxDsl.selectSampleByUserIdAndSerial(userId, sampleSerial)
            ).flatMap { tuple ->
                val userService = tuple.t1
                val sample = tuple.t2

                trxDsl.selectRequestByServiceIdAndSampleId(userService.serviceId!!, sample.id!!)
            }
        })
    }

    fun deleteRequestBySerial(userId: UUID, serviceSerial: String, sampleSerial: String): Mono<Void> {
        return Mono.from(dsl.transactionPublisher { trx ->
            val trxDsl = trx.dsl()
            Mono.zip(
                trxDsl.selectUserServiceByUserIdAndSerial(userId, serviceSerial).orUnprocessable(listOf(FieldError(field = "service.serial", message = "not found"))),
                trxDsl.selectSampleByUserIdAndSerial(userId, sampleSerial).orUnprocessable(listOf(FieldError(field = "sample.serial", message = "not found")))
            ).flatMap { tuple ->
                val userService = tuple.t1
                val sample = tuple.t2

                trxDsl.selectRequestByServiceIdAndSampleId(userService.serviceId!!, sample.id!!)
                    .orNotFound(listOf(FieldError(field = "sample.serial", message = "not found"), FieldError(field = "service.serial", message = "not found")))
                    .flatMap { request ->
                        if(request.isDeletable == true){
                            trxDsl.deleteRequestExtensionByRequestId(request.id!!)
                                .then(trxDsl.deleteRequestById(request.id!!))
                                .then(trxDsl.deletePatientById(request.patientId!!))
                                .then(trxDsl.deleteSampleById(request.sampleId!!))
                        } else { Mono.error(ConflictException(code = ErrorCode.DELETE_NOT_ALLOWED)) }

                    }
            }
        }).then()
    }

    fun saveRequest(userId: UUID, requestDTO: RequestDTO, serviceSerial: String, sampleSerial: String): Mono<RequestDTO> {
        return Mono.from(dsl.transactionPublisher { trx ->
            val trxDsl  = trx.dsl()
            Mono.zip(
                trxDsl.selectOrganizationByUserIdAndSerial(userId, requestDTO.organization!!.serial!!).orUnprocessable(listOf(FieldError(field = "organization.serial", message = "not found"))),
                trxDsl.selectUserServiceByUserIdAndSerial(userId, serviceSerial).orUnprocessable(listOf(FieldError(field = "service.serial", message = "not found"))),
                trxDsl.selectUserSampleTypeByUserIdAndSerial(userId, requestDTO.sample!!.type!!.serial!!).orUnprocessable(listOf(FieldError(field = "sample.type.serial", message = "not found")))
            ).flatMap { tuple ->
                val organization = tuple.t1
                val userService = tuple.t2
                val userSampleType = tuple.t3

                val reqPatient = requestDTO.patient!!.apply { id = UUID.randomUUID() }
                val reqSample = requestDTO.sample!!.apply {
                    id = UUID.randomUUID()
                    serial = sampleSerial
                    sampleTypeId = userSampleType.sampleTypeId
                }
                validateExtensions(trxDsl, userService.serviceId!!, requestDTO.extensions)
                    .then(trxDsl.insertPatient(reqPatient))
                    .then(trxDsl.insertOrGetSample(userId, reqSample)
                        .flatMap { ensuredSample ->
                            val conflicts = diffSample(ensuredSample, reqSample)
                            if (conflicts.isNotEmpty()) {
                                Mono.error(ConflictException(fieldErrors = conflicts))
                            } else {
                                Mono.just(ensuredSample)
                            }
                        }
                    )
                    .flatMap { ensuredSample ->
                        val reqRequest = requestDTO.apply {
                            id = UUID.randomUUID()
                            serviceId = userService.serviceId
                            sampleId = ensuredSample.id
                            organizationId = organization.id
                            patientId = reqPatient.id
                        }
                        trxDsl.insertRequest(reqRequest)
                            .mapUniqueViolation(
                                constraint = "uk_request_sample_id_service_id",
                                fieldErrors = listOf(
                                    FieldError(field = "sample.serial", message = "already exists", rejectedValue = reqSample.serial),
                                    FieldError(field = "serial", message = "already exists", rejectedValue = serviceSerial)
                                ),
                                code = ErrorCode.DUPLICATE_KEY
                            )
                            .then(insertExtensions(trxDsl, reqRequest.id!!, reqRequest.extensions))
                            .then(trxDsl.selectRequestById(reqRequest.id!!))
                    }
            }
        })
    }

    fun insertExtensions(trxDsl: DSLContext, requestId: UUID, requestExtensions: List<RequestExtensionDTO>?): Mono<Void> {
        return Flux.fromIterable(requestExtensions ?: emptyList())
            .flatMap { ext ->
                trxDsl.selectExtensionBycode(ext.code!!).flatMap { extension ->
                    val reqRequestExtension = RequestExtensionDTO(
                        id = UUID.randomUUID(),
                        value = ext.value,
                        requestId = requestId,
                        extensionId = extension.id
                    )
                    trxDsl.insertRequestExtension(reqRequestExtension)
                }
            }.then()
    }

    fun validateExtensions(trxDsl: DSLContext, serviceId: UUID, requestExtensions: List<RequestExtensionDTO>?): Mono<Void> {
        val requiredAndAllowedMono = trxDsl.selectServiceExtensionByServiceId(serviceId)
            .collectList()
            .map { rows ->
                val required = rows.filter { it.isRequired == true }
                    .map { it.extension!!.code }
                    .toSet()
                val allowed = rows.map { it.extension!!.code }.toSet()

                required to allowed
            }
        val providedMap = (requestExtensions ?: emptyList()).associate { it.code!! to (it.value ?: "") }
        val providedCodes = providedMap.keys

        return requiredAndAllowedMono.flatMap { (requiredCodes, allowedCodes) ->
            val missing = requiredCodes - providedCodes
            val invalid = providedCodes - allowedCodes

            if (missing.isNotEmpty() || invalid.isNotEmpty()) {
                val fieldErrors = buildList {
                    missing.forEach { add(FieldError(field = "extensions.code:$it", message = "required")) }
                    invalid.forEach { add(FieldError(field = "extensions.code:$it", message = "not allowed for this service")) }
                }
                return@flatMap Mono.error(UnprocessableEntityException(fieldErrors))
            } else {
                return@flatMap Mono.empty<Void>()
            }
        }
    }
    private fun diffSample(existing: SampleDTO, incoming: SampleDTO): List<FieldError> {
        val errs = mutableListOf<FieldError>()

        if (existing.sampleTypeId != incoming.sampleTypeId) {
            errs += FieldError(
                field = "sample.type.serial",
                message = "conflicts with existing sample",
                rejectedValue = incoming.type?.serial
            )
        }
        if (existing.samplingOn != incoming.samplingOn) {
            errs += FieldError(
                field = "sample.sampling_on",
                message = "conflicts with existing sample",
                rejectedValue = incoming.samplingOn
            )
        }
        if (existing.count != incoming.count) {
            errs += FieldError(
                field = "sample.count",
                message = "conflicts with existing sample",
                rejectedValue = incoming.count
            )
        }
        if (existing.age != incoming.age) {
            errs += FieldError(
                field = "sample.age",
                message = "conflicts with existing sample",
                rejectedValue = incoming.age
            )
        }
        return errs
    }
    fun patchRequest(userId: UUID, serviceSerial: String, sampleSerial: String, patch: RequestPatchDTO): Mono<RequestDTO> {
        return Mono.from(dsl.transactionPublisher { trx ->
            val trxDsl = trx.dsl()
            Mono.zip(
                trxDsl.selectUserServiceByUserIdAndSerial(userId, serviceSerial).orUnprocessable(listOf(FieldError("service.serial", "not found", serviceSerial))),
                trxDsl.selectSampleByUserIdAndSerial(userId, sampleSerial).orUnprocessable(listOf(FieldError("sample.serial", "not found", sampleSerial)))
            ).flatMap { tuple ->
                val userService = tuple.t1
                val sample = tuple.t2

                trxDsl.selectRequestByServiceIdAndSampleId(userService.serviceId!!, sample.id!!)
                    .orNotFound(listOf(FieldError(field = "sample.serial", message = "not found"), FieldError(field = "service.serial", message = "not found")))
                    .flatMap { current ->
                        if (current.isEditable == false) {
                            return@flatMap  Mono.error(ConflictException(code = ErrorCode.EDIT_NOT_ALLOWED))
                        }

                        val reqUpd = trxDsl.updateRequestById(current.id!!, patch)

                        val orgUpd: Mono<Boolean> =
                            if (patch.organization.isPresent && patch.organization.get().serial.isPresent) {
                                val orgSerial = patch.organization.get().serial.orElse(null)
                                    ?: return@flatMap Mono.error(UnprocessableEntityException(listOf(
                                        FieldError("organization.serial", "cannot be null")
                                    )))
                                trxDsl.selectOrganizationByUserIdAndSerial(userId, orgSerial)
                                    .orUnprocessable(listOf(FieldError("organization.serial", "not found", orgSerial)))
                                    .flatMap { org -> trxDsl.updateRequestOrganizationById(current.id!!, org.id!!) }
                            } else Mono.just(false)

                        val patientUpd =
                            if (patch.patient.isPresent) trxDsl.updatePatientFields(current.patientId!!, patch.patient.get())
                            else Mono.just(false)

                        if (patch.sample.isPresent) {
                            val s = patch.sample.get()
                            if (s.type.isPresent && s.type.get().serial.isPresent) {
                                return@flatMap Mono.error(UnprocessableEntityException(listOf(
                                    FieldError("sample.type.serial", "cannot be changed")
                                )))
                            }
                        }
                        val sampleUpd =
                            if (patch.sample.isPresent) trxDsl.updateSampleById(current.sampleId!!, patch.sample.get())
                            else Mono.just(false)

                        val extsUpd =
                            if (patch.extensions.isPresent) {
                                val list = patch.extensions.orElse(null) // null → 전체 삭제
                                if (list == null) {
                                    trxDsl.deleteRequestExtensionByRequestId(current.id!!).thenReturn(true)
                                } else {
                                    validateExtensions(trxDsl, current.serviceId!!, list)
                                        .then(trxDsl.deleteRequestExtensionByRequestId(current.id!!))
                                        .then(insertExtensions(trxDsl, current.id!!, list))
                                        .thenReturn(true)
                                }
                            } else Mono.just(false)

                        Mono.`when`(reqUpd, orgUpd, patientUpd, sampleUpd, extsUpd)
                        .then(trxDsl.selectRequestById(current.id!!))
                    }
            }
        })
    }
}