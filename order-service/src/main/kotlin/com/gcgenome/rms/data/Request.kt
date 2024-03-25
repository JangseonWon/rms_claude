package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

@JsonSubTypes(JsonSubTypes.Type(Patient::class, name = "patient"))
data class Request(
    @JsonProperty("order_id")
    val orderId: UUID?,
    @JsonProperty("service")
    val serviceId: String?,
    @JsonProperty("sample_id")
    val sampleId: UUID?,
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
    @JsonProperty("emp_id")
    val empId: String?,
    @JsonProperty("emp_name")
    val empName: String?,
    @JsonProperty("emp_mobile")
    val empMobile: String?,
    @JsonProperty("test")
    val test: Boolean?,
    @JsonProperty("credit")
    val credit: Boolean?,
    @JsonProperty("price")
    val price: Int?,
    @JsonProperty("outsourcing_cost")
    val outsourcingCost: Int?,
    @JsonProperty("patient")
    val patient: Patient?,
    @JsonProperty("sample")
    val sample: Sample?,
)