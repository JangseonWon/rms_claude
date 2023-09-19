package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(schema = "public", name = "order")
data class Order(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,
    @Column(name = "last_modify_at", nullable = false)
    val lastModifyAt: LocalDateTime,
    @ManyToOne
    @JoinColumn(name = "user_id")
    val userId: User,
    @Column(name = "test")
    val test: Boolean?,
    @Column(name = "credit")
    val credit: Boolean?,
    @Column(name = "price")
    val price: Int?,
    @Column(name = "outsourcing_cost")
    val outsourcingCost: Int?
)
