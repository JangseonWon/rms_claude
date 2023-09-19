package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(schema = "public", name = "user_service")
data class UserService(
    @EmbeddedId
    val pk: UserServicePK

){
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    lateinit var userId: User

    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    lateinit var serviceId: Service

    companion object {
        @Embeddable
        data class UserServicePK(
            @Column(name = "user_id") val userId: String,
            @Column(name = "service_id") val serviceId: String
        ) : Serializable
    }
}