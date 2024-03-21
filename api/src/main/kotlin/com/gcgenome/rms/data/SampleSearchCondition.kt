package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

data class SampleSearchCondition(
    @JsonProperty("filters")
    val filters: Filter,
    @JsonProperty("sort")
    val sort: Sort?
){
    companion object{
        data class Filter(
            @JsonProperty("create_at_from")
            @JsonDeserialize(using = LocalDateDeserializer::class)
            @JsonSerialize(using = LocalDateSerializer::class)
            val createAtFrom: LocalDate,
            @JsonProperty("create_at_to")
            @JsonDeserialize(using = LocalDateDeserializer::class)
            @JsonSerialize(using = LocalDateSerializer::class)
            val createAtTo: LocalDate,
            @JsonProperty("id")
            val barcode: String?,
            @JsonProperty("status")
            val status: Status?,
            @JsonProperty("serial")
            val serial: String?,
            @JsonProperty("sample_type_id")
            val sampleTypeId: String?,
            @JsonProperty("emp_id")
            val empId: String?,
            @JsonProperty("emp_name")
            val empName: String?,
            @JsonProperty("test")
            val test: Boolean?
        )
        data class Sort(
            @JsonProperty("field")
            val field: String,
            @JsonProperty("asc")
            val asc: Boolean = true
        )
    }
}




