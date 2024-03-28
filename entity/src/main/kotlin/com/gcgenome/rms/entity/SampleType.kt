package com.gcgenome.rms.entity

import jakarta.persistence.*

@Entity
@Table(schema = "rms_dev", name = "sample_type")
data class SampleType(
    @Id
    @Column(name = "id", length = 64)
    val id: String,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,

    @OneToMany(mappedBy = "sampleTypeId")
    val serviceSampleType: List<ServiceSampleType>,
    @OneToMany(mappedBy = "sampleTypeId")
    val sample: List<Sample>
)