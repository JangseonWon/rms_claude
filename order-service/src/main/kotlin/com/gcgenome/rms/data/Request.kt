package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.gcgenome.rms.tables.references.ORDER
import com.gcgenome.rms.tables.references.REQUEST
import org.jooq.Record
import java.time.LocalDateTime
import java.util.*

@JsonSubTypes(JsonSubTypes.Type(Patient::class, name = "patient"))
data class Request(
    @JsonProperty("order_id")
    var orderId: UUID?,
    @JsonProperty("service_id")
    var serviceId: String?,
    @JsonProperty("service")
    val service: Service?,
    @JsonProperty("sample_id")
    var sampleId: UUID?,
    @JsonProperty("serial")
    val serial: String?,
    @JsonProperty("user_service_id")
    val userServiceId: String?,
    @JsonProperty("status")
    val status: String?,
    @JsonProperty("memo")
    val memo: String?,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("ward")
    val ward: String?,
    @JsonProperty("physician")
    val physician: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("cart_at")
    var cartAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("specified_at")
    val specifiedAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("complete_at")
    val completeAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("resample_at")
    val resampleAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("last_modify_at")
    val lastModifyAt: LocalDateTime?,
    @JsonProperty("sample")
    val sample: Sample?
) {
    constructor(orderId: UUID, serviceId: String, sampleId: UUID) :
            this(orderId, serviceId, null, sampleId, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    companion object {
        fun toPatientModel(record: Record) =
            Request(
                orderId = record.get(ORDER.ID),
                serviceId = null,
                service = record.get("service", Service::class.java),
                sampleId = null,
                serial = record.get(ORDER.SERIAL),
                userServiceId = record.get(REQUEST.USER_SERVICE_ID),
                status = record.get(REQUEST.STATUS),
                memo = record.get(REQUEST.MEMO),
                department = record.get(REQUEST.DEPARTMENT),
                ward = record.get(REQUEST.WARD),
                physician = record.get(REQUEST.PHYSICIAN),
                createAt = record.get(REQUEST.CREATE_AT),
                cartAt = record.get(REQUEST.CART_AT),
                specifiedAt = record.get(REQUEST.SPECIFIED_AT),
                completeAt = record.get(REQUEST.COMPLETE_AT),
                resampleAt = record.get(REQUEST.RESAMPLE_AT),
                lastModifyAt = record.get(REQUEST.LAST_MODIFY_AT),
                sample = record.get("sample", Sample::class.java),
            )
    }
}

