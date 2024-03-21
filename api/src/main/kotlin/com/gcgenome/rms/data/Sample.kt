package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Sample(
    @JsonProperty("sample_id")
    var id: UUID?,
    @JsonProperty("status")
    val status: String?,
    @JsonProperty("id")
    val barcode: String?,
    @JsonProperty("serial")
    val serial: String?,
    @JsonProperty("quantity")
    val quantity: Int?,
    @JsonProperty("age")
    val age: Int?,
    @JsonProperty("retest_reason")
    val retestReason: String?,
    @JsonProperty("memo")
    val memo: String?,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("ward")
    val ward: String?,
    @JsonProperty("physician")
    val physician: String?,
    @JsonProperty("sampling")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    val sampling: LocalDate?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("specified_at")
    val specifiedAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("compete_at")
    val competeAt: LocalDateTime?,
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
    @JsonProperty("labs_test")
    val labsTest: Boolean?,
    @JsonProperty("labs_credit")
    val labsCredit: Boolean?,
    @JsonProperty("labs_price")
    val labsPrice: Int?,
    @JsonProperty("labs_outsourcing_cost")
    val labsOutsourcingCost: Int?,
    @JsonProperty("order_id")
    val orderId: UUID?,
    @JsonProperty("service_id")
    val serviceId: String?,
    @JsonProperty("sample_type_id")
    val sampleTypeId: String?,
    @JsonProperty("patient_serial")
    val patientSerial: String?,
    @JsonProperty("organization_id")
    val organizationId: String?,
    @JsonProperty("user_id")
    val userId: String?,
    @JsonProperty("patient")
    val patient: Patient?,
    @JsonProperty("extensions")
    val extensions: Array<Extension>?,
    @JsonProperty("reports")
    val reports: Array<Report>?

)
