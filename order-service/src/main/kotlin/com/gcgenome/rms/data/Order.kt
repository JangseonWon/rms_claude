package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.gcgenome.rms.tables.references.ORDER
import org.jooq.JSON
import org.jooq.Record4
import java.time.LocalDateTime
import java.util.*

data class Order(
    @JsonProperty("serial")
    val serial: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonProperty("create_at")
    val createAt: LocalDateTime?,
    @JsonProperty("user_id")
    val userId: String?,
    @JsonProperty("requests")
    val requests: List<Request>?
) {
    companion object {
        fun toModel(record: Record4<String?, LocalDateTime?, String?, JSON?>) =
            Order(
                serial = record.getValue(ORDER.SERIAL),
                createAt = record.get(ORDER.CREATE_AT),
                userId = record.getValue(ORDER.USER_ID),
                requests = record.getValue("requests", Array<Request>::class.java).toList()
            )
    }
}

