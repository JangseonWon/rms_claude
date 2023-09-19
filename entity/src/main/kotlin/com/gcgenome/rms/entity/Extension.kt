package com.gcgenome.rms.entity

import jakarta.persistence.*

@Entity
@Table(schema = "public", name = "extension")
data class Extension(
    @Id
    @Column(name = "id", length = 64)
    val id: String ="",
    @Column(name = "name", length = 64)
    val name: String ="",
    @Column(name = "regex", length = 64)
    val regex: String ="",
    @OneToMany(mappedBy = "extensionId")
    val serviceExtension: List<ServiceExtension>,
    @OneToMany(mappedBy = "extensionId")
    val sampleExtension: List<SampleExtension>
)
