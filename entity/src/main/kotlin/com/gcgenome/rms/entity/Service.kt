package com.gcgenome.rms.entity

import jakarta.persistence.*

@Entity
@Table(schema = "public", name = "service")
data class Service(
    @Id
    @Column(name = "id", length = 8)
    val id: String = "",
    @Column(name = "name", length = 64)
    val name: String = "",
    @OneToMany(mappedBy = "serviceId")
    val serviceSampleType: List<ServiceSampleType>,
    @OneToMany(mappedBy = "serviceId")
    val serviceExtension: List<ServiceExtension>

)
