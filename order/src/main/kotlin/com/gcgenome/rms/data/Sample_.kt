package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.gcgenome.lims.tables.records.SampleRecord
import com.gcgenome.lims.tables.references.SAMPLE
import org.jooq.Record1
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@JsonSubTypes(JsonSubTypes.Type(Extension_::class, name = "extensions"))
data class Sample_(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("type_id")
    val typeId: String?,
    @JsonProperty("registration_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    val registrationAt: LocalDateTime?,
    @JsonProperty("organization_id")
    val organizationId: String?,
    @JsonProperty("serial")
    val serial: String?,
    @JsonProperty("age")
    val age: Int?,
    @JsonProperty("sampling")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    val sampling: LocalDate?,
    @JsonProperty("note")
    val note: String?,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("ward")
    val ward: String?,
    @JsonProperty("physician")
    val physician: String?,
    @JsonProperty("state")
    val state: String?,
    @JsonProperty("extensions")
    val extensions: List<Extension_>?
){
    companion object {
        fun toModel(record: Record1<SampleRecord>) =
            Sample_(
                id = record.get(SAMPLE.ID),
                typeId = record.get(SAMPLE.SAMPLE_TYPE_ID),
                registrationAt = record.get(SAMPLE.REGISTRATION_AT),
                organizationId = record.get(SAMPLE.ORGANIZATION_ID),
                serial = record.get(SAMPLE.SERIAL),
                age = record.get(SAMPLE.AGE),
                sampling = record.get(SAMPLE.SAMPLING)?.toLocalDate(),
                note = record.get(SAMPLE.NOTE),
                department = record.get(SAMPLE.DEPARTMENT),
                ward = record.get(SAMPLE.WARD),
                physician = record.get(SAMPLE.PHYSICIAN),
                state = record.get(SAMPLE.STATE),
                extensions = record.getValue("extensions", Array<Extension_>::class.java).toList()
            )
    }
}
