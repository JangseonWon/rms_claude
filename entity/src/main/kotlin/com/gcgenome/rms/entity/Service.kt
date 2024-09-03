package com.gcgenome.rms.entity

import jakarta.persistence.*

@Entity
@Table(schema = "rms_dev", name = "service")
data class Service(
    @Id
    @Column(name = "id", length = 8)
    val id: String,
    @Column(name = "name", length = 128, nullable = false)
    val name: String,

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false, nullable = true)
    val categoryId: Category,

    @OneToMany(mappedBy = "serviceId")
    val user: List<UserService>,
    @OneToMany(mappedBy = "serviceId")
    val request: List<Request>,
    @OneToMany(mappedBy = "serviceId")
    val serviceSampleType: List<ServiceSampleType>,
    @OneToMany(mappedBy = "serviceId")
    val serviceExtension: List<ServiceExtension>


)
