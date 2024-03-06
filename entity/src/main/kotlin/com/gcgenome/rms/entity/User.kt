package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(schema = "rms_dev", name = "user")
data class User(
    @Id
    @Column(name = "id", length = 64)
    val id: String,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,
    @Column(name = "password", length = 64, nullable = false)
    val password: String,
    @Column(name = "role", length = 64, nullable = false)
    val role: String,
    @Column(name = "type", length = 64, nullable = false)
    val type: String,
    @Column(name = "email", length = 64, nullable = true)
    val email: String,
    @Column(name = "phone_number", length = 64, nullable = true)
    val phoneNumber: String,
    @Column(name = "key", length = 64, nullable = true)
    val key: UUID,
    @Column(name = "state", length = 64, nullable = false)
    val state: String,
    @Column(name = "branch_serial", length = 64, nullable = false)
    val branchSerial: String,
    @Column(name = "branch_name", length = 64, nullable = false)
    val branchName: String,

    @OneToMany(mappedBy = "userId")
    val organization: List<Organization>,
    @OneToMany(mappedBy = "userId")
    val userService: List<UserService>,
    @OneToMany(mappedBy = "userId")
    val order: List<Order>
)
