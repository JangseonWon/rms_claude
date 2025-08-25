package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "service",
    uniqueConstraints = [UniqueConstraint(name = "uk_service_code", columnNames = ["code"])]
)
data class Service(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "code", length = 64, nullable = false)
    val code: String,
    @Column(name = "name_kr", length = 64, nullable = true)
    val nameKr: String,
    @Column(name = "name_er", length = 64, nullable = true)
    val nameEr: String,
    @Column(name = "type", length = 64, nullable = true)
    val type: String,

    @OneToMany(mappedBy = "serviceId")
    val userServices: List<UserService>,
    @OneToMany(mappedBy = "serviceId")
    val serviceSampleTypes: List<ServiceSampleType>,
    @OneToMany(mappedBy = "serviceId")
    val serviceExtensions: List<ServiceExtension>,
    @OneToMany(mappedBy = "serviceId")
    val requests: List<Request>
)
