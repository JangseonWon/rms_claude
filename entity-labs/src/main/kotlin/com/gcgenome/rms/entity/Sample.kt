package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "sample",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_sample_barcode", columnNames = ["barcode"]),
        UniqueConstraint(name = "uk_sample_user_serial", columnNames = ["user_id", "serial"])
    ]
)
data class Sample(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "barcode", length = 64, nullable = true)
    val barcode: String,
    @Column(name = "serial", length = 64, nullable = true)
    val serial: String,
    @Column(name = "count", nullable = true)
    val count: Int,
    @Column(name = "age", nullable = true)
    val age: Int,
    @Column(name = "sampling_on", nullable = false)
    val samplingOn: LocalDate,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,

    @OneToMany(mappedBy = "sampleId")
    val requests: List<Request>,

    @ManyToOne
    @JoinColumn(name = "sample_type_id", insertable = false, updatable = false, nullable = false)
    val sampleTypeId: SampleType,
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,
)
