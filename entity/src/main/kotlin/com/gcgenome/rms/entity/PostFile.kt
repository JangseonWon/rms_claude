package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(schema = "rms_dev", name = "post_file")
data class PostFile(
    @Id
    @Column(name = "id")
    val id: Long,
    @Column(name = "path", nullable = false)
    val path: String,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false, nullable = false)
    val postId: Post,
)
