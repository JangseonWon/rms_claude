package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(schema = "rms_dev", name = "order")
data class Order(
    @Id
    @Column(name = "id")
    val id: UUID,

    @OneToMany(mappedBy = "orderId")
    val item: List<Item>,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,
)
