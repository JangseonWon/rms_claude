package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "post")
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,
    @Column(name = "title", length = 255, nullable = false)
    val title: String,
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
    @JoinColumn(name = "post_category_id", insertable = false, updatable = false, nullable = false)
    val postCategoryId: PostCategory,

    @OneToMany(mappedBy = "postId")
    val postFile: List<PostFile>,
    @OneToMany(mappedBy = "postId")
    val comments: List<Comment>,
    @OneToMany(mappedBy = "postId")
    val postRead: List<PostRead>
)
