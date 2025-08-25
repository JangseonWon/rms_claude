package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "user_sample_type",
    uniqueConstraints = [UniqueConstraint(name = "uk_user_sample_type_user_id_sample_type_id_serial", columnNames = ["user_id", "sample_type_id", "serial"])]
)
data class UserSampleType(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "serial", length = 64, nullable = false)
    val serial: String,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,
    @ManyToOne
    @JoinColumn(name = "sample_type_id", insertable = false, updatable = false, nullable = false)
    val sampleTypeId: SampleType,
)