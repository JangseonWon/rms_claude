package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

data class SearchCondition(
    @JsonProperty("filter")
    val filter: Filter,
    @JsonProperty("sort")
    val sort: Sort?
)
data class Filter(
    @JsonProperty("order")
    val order: OrderFilter?,
    @JsonProperty("request")
    val request: RequestFilter,
    @JsonProperty("sample")
    val sample: SampleFilter?,
    @JsonProperty("patient")
    val patient: PatientFilter?
){
    companion object{
        data class OrderFilter(
            @JsonProperty("serial")
            val serial: String?
        )
        data class RequestFilter(
            @JsonProperty("service")
            val service: Service?,
            @JsonProperty("user_service_id")
            val userServiceId: String?,
            @JsonProperty("date_from")
            @JsonDeserialize(using = LocalDateDeserializer::class)
            @JsonSerialize(using = LocalDateSerializer::class)
            val dateFrom: LocalDate,
            @JsonProperty("date_to")
            @JsonDeserialize(using = LocalDateDeserializer::class)
            @JsonSerialize(using = LocalDateSerializer::class)
            val dateTo: LocalDate,
            @JsonProperty("status")
            val status: Status?,
            @JsonProperty("emp_id")
            val empId: String?,
            @JsonProperty("emp_name")
            val empName: String?,
            @JsonProperty("test")
            val test: Boolean?
        )
        data class SampleFilter(
            @JsonProperty("barcode")
            val barcode: String?,
            @JsonProperty("user_sample_id")
            val userSampleId: String?,
            @JsonProperty("sample_type")
            val sampleType: SampleType?
        )
        data class PatientFilter(
            @JsonProperty("serial")
            val patientSerial: String?,
            @JsonProperty("name")
            val name: String?,
            @JsonProperty("organization_id")
            val organizationId: String?
        )
    }
}

data class Sort(
    @JsonProperty("field")
    val field: String?,
    @JsonProperty("asc")
    val asc: Boolean?
)