package com.gcgenome.rms.entity

import jakarta.persistence.*

@Entity
@Table(schema = "public", name = "sample_type")
data class SampleType(
    @Id
    @Column(name = "id", length = 8)
    val id: String = "",
    @Column(name = "name", length = 64)
    val name: String = "",
    @OneToMany(mappedBy = "sampleTypeId")
    val serviceSampleType: List<ServiceSampleType>,
    @OneToMany(mappedBy = "sampleTypeId")
    val sample: List<Sample>
)