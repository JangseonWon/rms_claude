package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@JsonSubTypes(JsonSubTypes.Type(Extension::class, name = "extensions"))
data class Sample(
    @JsonProperty("id")
    var id: UUID?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("last_modify_at")
    val lastModifyAt: LocalDateTime?,
    @JsonProperty("age")
    val age: Int?,
    @JsonProperty("sampling")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    @JsonSerialize(using = LocalDateSerializer::class)
    val sampling: LocalDate?,
    @JsonProperty("note")
    val note: String?,
    @JsonProperty("genome_barcode")
    val genomeBarcode: String?,
    @JsonProperty("sample_barcode")
    val sampleBarcode: String?,
    @JsonProperty("type")
    val typeId: String?,
    @JsonProperty("department")
    val department: String?,
    @JsonProperty("ward")
    val ward: String?,
    @JsonProperty("physician")
    val physician: String?,
    @JsonProperty("extensions")
    val extensions: List<Extension>?,
    @JsonProperty("state")
    val state: String?,
    @JsonProperty("registration_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    val registrationAt: LocalDateTime?,
    @JsonProperty("organization_id")
    val organizationId: String?,
    @JsonProperty("item_id")
    var itemId: UUID?,
){
    /*companion object {
        fun toModel(record: Record1<SampleRecord>) =
            Sample_(
                id = record.get(SAMPLE.ID),
                createAt = record.get(SAMPLE.CREATE_AT),
                lastModifyAt = record.get(SAMPLE.LAST_MODIFY_AT),
                age = record.get(SAMPLE.AGE),
                sampling = record.get(SAMPLE.SAMPLING)?.toLocalDate(),
                note = record.get(SAMPLE.NOTE),
                genomeBarcode = record.get(SAMPLE.GENOME_BARCODE),
                sampleBarcode = record.get(SAMPLE.SAMPLE_BARCODE),
                typeId = record.get(SAMPLE.SAMPLE_TYPE_ID),
                registrationAt = record.get(SAMPLE.REGISTRATION_AT),
                organizationId = record.get(SAMPLE.ORGANIZATION_ID),
                department = record.get(SAMPLE.DEPARTMENT),
                ward = record.get(SAMPLE.WARD),
                physician = record.get(SAMPLE.PHYSICIAN),
                state = record.get(SAMPLE.STATE),
                extensions = record.getValue("extensions", Array<Extension_>::class.java).toList()
            )
    }*/
}
