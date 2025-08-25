package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "user",
    uniqueConstraints = [UniqueConstraint(name = "uk_user_login_id", columnNames = ["login_id"])]
)
data class User(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "login_id", length = 64, nullable = false)
    val loginId: String,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,
    @Column(name = "password", length = 64, nullable = false)
    val password: String,
    @Column(name = "role", length = 64, nullable = false)
    val role: String,
    @Column(name = "email", length = 64, nullable = true)
    val email: String,
    @Column(name = "key", length = 64, nullable = true)
    val key: UUID,
    @Column(name = "state", length = 64, nullable = false)
    val state: String,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,

    @OneToMany(mappedBy = "userId")
    val organizations: List<Organization>,
    @OneToMany(mappedBy = "userId")
    val userServices: List<UserService>,
    @OneToMany(mappedBy = "userId")
    val userSampleTypes: List<UserSampleType>,
    @OneToMany(mappedBy = "userId")
    val samples: List<Sample>
)
