package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(schema = "rms_dev", name = "post_category")
data class PostCategory(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,

    @OneToMany(mappedBy = "postCategoryId")
    val posts: List<Post>
)
