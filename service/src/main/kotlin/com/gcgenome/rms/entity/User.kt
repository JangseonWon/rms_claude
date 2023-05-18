package com.gcgenome.rms.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("rms.user")
data class User(
    @Id @Column("id") val id: String,
    @Column("authority") val authority: String,
    @Column("department") val department: String? = null,
    @Column("key") val key: UUID? = null,
    @Column("name") val name: String,
    @Column("password") val password: String,
    @Column("state") val state: String
)