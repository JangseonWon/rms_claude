package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(schema = "rms_dev", name = "post_read")
data class PostRead(
    @EmbeddedId
    val pk: PostReadPK,
    @Column(name = "read_at", nullable = true)
    val readAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false, nullable = false)
    val postId: Post,
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User
){
    @Embeddable
    data class PostReadPK(
        @Column(name = "post_id") val postId: Long,
        @Column(name = "user_id") val userId: String
    ) : Serializable

}