package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "patient"
)
data class Patient(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "serial", length = 64, nullable = false)
    val serial: String,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,
    @Column(name = "sex", length = 64, nullable = true)
    val sex: String,
    @Column(name = "birth", nullable = false)
    val birth: LocalDate,

    @OneToOne(mappedBy = "patientId")
    val request: Request,
)
