package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "sample_type",
    uniqueConstraints = [UniqueConstraint(name = "uk_sample_type_code", columnNames = ["code"])]
)
data class SampleType(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "code", length = 64, nullable = false)
    val code: String,
    @Column(name = "name_kr", length = 64, nullable = true)
    val nameKr: String,
    @Column(name = "name_en", length = 64, nullable = true)
    val nameEn: String,

    @OneToMany(mappedBy = "sampleTypeId")
    val serviceSampleTypes: List<ServiceSampleType>,
    @OneToMany(mappedBy = "sampleTypeId")
    val userSampleTypes: List<UserSampleType>,
    @OneToMany(mappedBy = "sampleTypeId")
    val samples: List<Sample>,
)