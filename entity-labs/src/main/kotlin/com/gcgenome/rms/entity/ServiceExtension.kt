package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "service_extension",
    uniqueConstraints = [UniqueConstraint(name = "uk_service_extension_service_id_extension_id", columnNames = ["service_id", "extension_id"])]
)
data class ServiceExtension(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "is_required", nullable = false)
    val isRequired: Boolean,

    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false, nullable = false)
    val serviceId: Service,
    @ManyToOne
    @JoinColumn(name = "extension_id", insertable = false, updatable = false, nullable = false)
    val extensionId: Extension,
)
