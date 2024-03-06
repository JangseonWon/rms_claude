package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "report")
data class Report(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "type", length = 64, nullable = false)
    val type: String,
    @Column(name = "value", nullable = false)
    val value: String,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,
    @Column(name = "reported_at", nullable = true)
    val reportedAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "sample_id", insertable = false, updatable = false, nullable = false)
    val sampleId: Sample
)
