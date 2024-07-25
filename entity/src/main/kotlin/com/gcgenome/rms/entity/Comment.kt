package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(schema = "rms_dev", name = "comment")
data class Comment(
    @Id
    @Column(name = "id", length = 64)
    val id: UUID,
    @Column(name = "content", columnDefinition = "TEXT")
    val content: String,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,
    @Column(name = "last_modify_at", nullable = false)
    val lastModifyAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,
    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false, nullable = false)
    val postId: Post,
)
