package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(schema = "public", name = "service_extension")
data class ServiceExtension(
    @EmbeddedId
    val pk: ServiceExtensionPK,
    @Column(name = "required")
    val required: Boolean

){
    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    lateinit var serviceId: Service
    @ManyToOne
    @JoinColumn(name = "extension_id", insertable = false, updatable = false)
    lateinit var extensionId: Extension

    companion object {
        @Embeddable
        data class ServiceExtensionPK(
            @Column(name = "service_id") val serviceId: String,
            @Column(name = "extension_id") val extensionId: String
        ) : Serializable
    }
}
