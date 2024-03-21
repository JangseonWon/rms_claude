package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(schema = "rms_dev2", name = "service_extension")
data class ServiceExtension(
    @EmbeddedId
    val pk: ServiceExtensionPK,
    @Column(name = "required", nullable = false)
    val required: Boolean,

    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    val serviceId: Service,
    @ManyToOne
    @JoinColumn(name = "extension_id", insertable = false, updatable = false)
    val extensionId: Extension
){
    @Embeddable
    data class ServiceExtensionPK(
        @Column(name = "service_id") val serviceId: String,
        @Column(name = "extension_id") val extensionId: String
    ) : Serializable
}
