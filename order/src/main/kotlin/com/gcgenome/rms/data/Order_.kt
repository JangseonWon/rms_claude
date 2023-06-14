package com.gcgenome.rms.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.gcgenome.lims.tables.references.ORDER
import org.jooq.JSON
import org.jooq.Record6
import java.util.UUID

data class Order_(
    @JsonProperty("id")
    var id: UUID?,
    @JsonProperty("test")
    val test: Boolean?,
    @JsonProperty("credit")
    val credit: Boolean?,
    @JsonProperty("price")
    val price: Int?,
    @JsonProperty("outsourcing_cost")
    val outsourcingCost:Int?,
    @JsonProperty("items")
    val items: List<Item_>
){
    companion object {
        fun toModel(record: Record6<UUID?, Boolean?, Boolean?, Int?, Int?, JSON?>) =
            Order_(
                id = record.getValue(ORDER.ID),
                test = record.getValue(ORDER.TEST),
                credit = record.getValue(ORDER.CREDIT),
                price = record.getValue(ORDER.PRICE),
                outsourcingCost = record.getValue(ORDER.OUTSOURCING_COST),
                items = record.getValue("items", Array<Item_>::class.java).toList()
            )
    }
}
