package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.gcgenome.lims.tables.references.ORDER
import org.jooq.JSON
import org.jooq.Record8
import java.time.LocalDateTime
import java.util.*

data class Order(
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
    @JsonProperty("test")
    val test: Boolean?,
    @JsonProperty("credit")
    val credit: Boolean?,
    @JsonProperty("price")
    val price: Int?,
    @JsonProperty("outsourcing_cost")
    val outsourcingCost:Int?,
    @JsonProperty("items")
    val items: List<Item>?
){
    companion object {
        fun toModel(record: Record8<UUID?, LocalDateTime?, LocalDateTime?, Boolean?, Boolean?, Int?, Int?, JSON?>) =
            Order(
                id = record.get(ORDER.ID),
                createAt = record.get(ORDER.CREATE_AT),
                lastModifyAt = record.get(ORDER.LAST_MODIFY_AT),
                test = record.getValue(ORDER.TEST),
                credit = record.getValue(ORDER.CREDIT),
                price = record.getValue(ORDER.PRICE),
                outsourcingCost = record.getValue(ORDER.OUTSOURCING_COST),
                items = record.getValue("items", Array<Item>::class.java).toList()
            )
    }
}
