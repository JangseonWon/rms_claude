package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "user_service",
    uniqueConstraints = [UniqueConstraint(name = "uk_user_service_user_id_service_id_serial",columnNames = ["user_id", "service_id", "serial"])]
)
data class UserService(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "serial", length = 64, nullable = false)
    val serial: String,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,
    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false, nullable = false)
    val serviceId: Service,
)
