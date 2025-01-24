package com.gcgenome.rms.entity

import jakarta.persistence.*

@Entity
@Table(schema = "rms_dev", name = "request_relation")
data class RequestRelation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int,
    @Column(name = "name", nullable = false)
    val name: String,

    @OneToMany(mappedBy = "requestRelationId")
    val requests: List<Request>
)
