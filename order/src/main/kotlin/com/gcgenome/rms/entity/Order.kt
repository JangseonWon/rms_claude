package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
@Schema("rms")
@Table(name = "order", schema = "rms")
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
