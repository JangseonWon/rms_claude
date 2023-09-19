package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "public", name = "report")
data class Report(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "create_at")
    val createAt: LocalDateTime,
    @ManyToOne
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    val sampleId: Sample,
    @Column(name = "type", length = 64)
    val type: String,
    @Column(name = "value")
    val value: String,
    @Column(name = "complete_at")
    val completeAt: LocalDateTime
)
