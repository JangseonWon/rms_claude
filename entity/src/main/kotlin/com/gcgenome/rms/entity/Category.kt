package com.gcgenome.rms.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(schema = "rms_dev2", name = "category")
data class Category(
    @Id
    @Column(name = "id", length = 64, nullable = false)
    val id: UUID,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,
    @Column(name = "order_type", length = 64, nullable = false)
    val orderType: String,

    @OneToMany(mappedBy = "categoryId")
    val service: List<Service>,
)
