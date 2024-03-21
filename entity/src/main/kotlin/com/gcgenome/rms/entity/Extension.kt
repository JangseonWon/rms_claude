package com.gcgenome.rms.entity

import jakarta.persistence.*

@Entity
@Table(schema = "rms_dev2", name = "extension")
data class Extension(
    @Id
    @Column(name = "id", length = 64, nullable = false)
    val id: String,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,
    @Column(name = "regex", length = 64, nullable = false)
    val regex: String,

    @OneToMany(mappedBy = "extensionId")
    val serviceExtension: List<ServiceExtension>,
    @OneToMany(mappedBy = "extensionId")
    val sampleExtension: List<SampleExtension>
)
