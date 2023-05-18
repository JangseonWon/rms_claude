package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Schema("rms")
@Table("service_extension")
data class ServiceExtension(
    @Column("service_id") val serviceId: String,
    @Column("extension_id") val extensionId: String,
    @Column("required") val required: Boolean
) {
    @Id
    @Transient
    val _id: ServiceExtensionPK = ServiceExtensionPK(serviceId, extensionId)
    companion object {
        data class ServiceExtensionPK(
            val serviceId: String,
            val extensionId: String
        )
    }
}
