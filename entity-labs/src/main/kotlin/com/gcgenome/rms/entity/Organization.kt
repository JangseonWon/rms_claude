package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "organization",
    uniqueConstraints = [UniqueConstraint(name = "uk_organization_user_id_serial", columnNames = ["user_id", "serial"])]
)
data class Organization(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "serial", length = 64, nullable = false)
    val serial: String,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,
    @Column(name = "registration_number", length = 64, nullable = true)
    val registration_number: String,
    @Column(name = "nursing_number", length = 64, nullable = true)
    val nursing_number: String,
    @Column(name = "branch_code", length = 64, nullable = true)
    val branch_code: String,
    @Column(name = "branch_name", length = 64, nullable = true)
    val branch_name: String,
    @Column(name = "employee_id", length = 64, nullable = true)
    val employee_id: String,
    @Column(name = "employee_name", length = 64, nullable = true)
    val employee_name: String,
    @Column(name = "employee_phone", length = 64, nullable = true)
    val employee_phone: String,
    @Column(name = "type", length = 64, nullable = true)
    val type: String,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,

    @OneToMany(mappedBy = "organizationId")
    val requests: List<Request>,
)
