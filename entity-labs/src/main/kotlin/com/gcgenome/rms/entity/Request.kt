package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "request",
    uniqueConstraints = [UniqueConstraint(name = "uk_request_sample_id_service_id", columnNames = ["sample_id", "service_id"])]
)
data class Request(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "department", length = 64, nullable = true)
    val department: String,
    @Column(name = "ward", length = 64, nullable = true)
    val ward: String,
    @Column(name = "physician", length = 64, nullable = true)
    val physician: String,
    @Column(name = "memo", length = 64, nullable = true)
    val memo: String,
    @Column(name = "genome_price", nullable = true)
    val genomePrice: Long,
    @Column(name = "labs_price", nullable = true)
    val labsPrice: Long,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,
    @Column(name = "is_editable", nullable = false)
    val isEditable: Boolean,
    @Column(name = "is_deletable", nullable = false)
    val isDeletable: Boolean,

    @ManyToOne
    @JoinColumn(name = "organization_id", insertable = false, updatable = false, nullable = false)
    val organizationId: Organization,
    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false, nullable = false)
    val serviceId: Service,
    @ManyToOne
    @JoinColumn(name = "sample_id", insertable = false, nullable = false, unique = true)
    val sampleId: Sample,

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    val patientId: Patient,


    @OneToMany(mappedBy = "requestId")
    val requestExtensions: List<RequestExtension>,
)