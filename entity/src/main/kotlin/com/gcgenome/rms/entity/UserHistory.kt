package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(schema = "rms_dev", name = "user_history")
data class UserHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,
    @Column(name = "field_name", nullable = false, length = 64)
    val fieldName: String,
    @Column(name = "old_value", nullable = true, length = 255)
    val oldValue: String,
    @Column(name = "new_value", nullable = false, length = 255)
    val newValue: String,
    @Column(name = "changed_by", nullable = false, length = 64)
    val changedBy: String,
    @Column(name = "changed_at", nullable = true)
    val changedAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,
)