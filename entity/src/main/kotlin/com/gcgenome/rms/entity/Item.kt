package com.gcgenome.rms.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "item")
data class Item(
    @EmbeddedId
    val pk: ItemPK,
    @Column(name = "serial", length = 64, nullable = true)
    val serial: String,

    @OneToMany(mappedBy = "itemId")
    val sample: List<Sample>,

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    val orderId: Order,
    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    val serviceId: Service
){
    @Embeddable
    data class ItemPK(
        @Column(name = "order_id") val orderId: UUID,
        @Column(name = "service_id") val serviceId: String
    ) : Serializable
}