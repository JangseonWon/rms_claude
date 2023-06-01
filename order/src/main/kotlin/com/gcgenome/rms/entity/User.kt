package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Schema("rms")
@Table(name = "user", schema = "rms")
data class User(
    @Id @Column("id") val id: String,
    @Column("authority") val authority: String,
    @Column("department") val department: String? = null,
    @Column("key") val key: UUID? = null,
    @Column("name") val name: String,
    @Column("password") val password: String,
    @Column("state") val state: String
)