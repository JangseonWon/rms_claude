package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "request_group")
data class RequestGroup(
    @Id
    @Column(name = "id")
    val id: UUID,

    @OneToMany(mappedBy = "requestGroupId")
    val requests: List<Request>,
)
