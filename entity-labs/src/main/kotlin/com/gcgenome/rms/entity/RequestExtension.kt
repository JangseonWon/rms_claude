package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "request_extension"
)
data class RequestExtension(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "value", length = 64, nullable = false)
    val value: String,

    @ManyToOne
    @JoinColumn(name = "request_id", insertable = false, updatable = false, nullable = false)
    val requestId: Request,
    @ManyToOne
    @JoinColumn(name = "extension_id", insertable = false, updatable = false, nullable = false)
    val extensionId: Extension,
)
