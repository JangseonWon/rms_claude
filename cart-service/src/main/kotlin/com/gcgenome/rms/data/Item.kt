package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.gcgenome.rms.tables.references.ITEM
import org.jooq.JSON
import org.jooq.Record3
import java.util.*

@JsonSubTypes(JsonSubTypes.Type(Patient::class, name = "patient"))
data class Item(
    @JsonProperty("order_id")
    val orderId: UUID?,
    @JsonProperty("service")
    val serviceId: String,
    @JsonProperty("serial")
    val serial: String,
    @JsonProperty("patient")
    val patient: Patient
) {
    companion object {
        fun toModel (record: Record3<String?, String?, JSON>) =
            Item(
                orderId = null,
                serviceId = record.get(ITEM.SERVICE_ID)!!,
                serial = record.get(ITEM.SERIAL)!!,
                patient = record.get("patient", Patient::class.java)
            )
    }
}