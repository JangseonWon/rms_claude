package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(schema = "rms_dev", name = "order", indexes = [
    Index(unique = true, columnList = "serial")
])
data class Order(
    @Id
    @Column(name = "id")
    val id: UUID,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,

    @OneToMany(mappedBy = "orderId")
    val requests: List<Request>
)
