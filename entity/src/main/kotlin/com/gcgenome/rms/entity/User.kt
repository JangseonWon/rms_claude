package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(schema = "public", name = "user")
data class User(
    @Id
    @Column(name = "id", length = 64)
    val id: String = "",
    @Column(name = "name", length = 64)
    val name: String = "",
    @Column(name = "password", length = 64)
    val password: String = "",
    @Column(name = "authority", length = 64)
    val authority: String = "",
    @Column(name = "department", length = 64)
    val department: String = "",
    @Column(name = "code")
    val code: Short?,
    @Column(name = "key", length = 64)
    val key: UUID?,
    @Column(name = "state", length = 64)
    val state: String = "",
    @OneToMany(mappedBy = "userId")
    val serviceExtension: List<Organization>
)
