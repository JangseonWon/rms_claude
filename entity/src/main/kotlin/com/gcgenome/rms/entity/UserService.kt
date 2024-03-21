package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(schema = "rms_dev2", name = "user_service")
data class UserService(
    @EmbeddedId
    val pk: UserServicePK,
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User,
    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false, nullable = false)
    val serviceId: Service
){
    @Embeddable
    data class UserServicePK(
        @Column(name = "user_id") val userId: String,
        @Column(name = "service_id") val serviceId: String
    ) : Serializable

}