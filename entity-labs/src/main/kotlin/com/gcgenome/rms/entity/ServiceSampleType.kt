package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "service_sample_type",
    uniqueConstraints = [UniqueConstraint(name = "uk_service_sample_type_service_id_sample_type_id",columnNames = ["service_id", "sample_type_id"])]
)
data class ServiceSampleType(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false, nullable = false)
    val serviceId: Service,
    @ManyToOne
    @JoinColumn(name = "sample_type_id", insertable = false, updatable = false, nullable = false)
    val sampleTypeId: SampleType,
)
