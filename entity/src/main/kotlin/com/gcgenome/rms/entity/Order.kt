package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(schema = "rms_dev2", name = "order")
data class Order(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "serial", nullable = true, unique = true)
    val serial: String,
    @Column(name = "create_at", nullable = true)
    val createAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,

    @OneToMany(mappedBy = "orderId")
    val item: List<Request>
)
