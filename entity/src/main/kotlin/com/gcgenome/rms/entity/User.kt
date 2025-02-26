package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
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
    @Column(name = "branch_serial", length = 64, nullable = true)
    val branchSerial: String,
    @Column(name = "branch_name", length = 64, nullable = true)
    val branchName: String,
    @Column(name = "employee_department", length = 64, nullable = true)
    val employeeDepartment: String,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,
    @Column(name = "last_password_changed_at", nullable = false)
    val lastPasswordChangedAt: LocalDateTime,

    @OneToMany(mappedBy = "userId")
    val organizations: List<Organization>,
    @OneToMany(mappedBy = "userId")
    val userServices: List<UserService>,
    @OneToMany(mappedBy = "userId")
    val posts: List<Post>,
    @OneToMany(mappedBy = "userId")
    val comments: List<Comment>,
    @OneToMany(mappedBy = "userId")
    val postRead: List<PostRead>,
    @OneToMany(mappedBy = "userId")
    val requests: List<Request>
    )
