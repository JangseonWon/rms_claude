package com.gcgenome.rms.entity

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("rms.order")
data class Order(
    @Id @Column("id") val _id: UUID,
    @Column("user_id") val userId: String,
    @Column("test") val test: Boolean?,
    @Column("credit") val credit: Boolean?,
    @Column("price") val price: Int?,
    @Column("outsourcing_cost") val outsourcingCost: Int?
):Persistable<UUID> {
    override fun getId(): UUID = _id
    override fun isNew(): Boolean = true

}
