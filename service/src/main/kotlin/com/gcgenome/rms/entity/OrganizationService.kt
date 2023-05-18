package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Schema("rms")
@Table("organization_service")
data class OrganizationService(
    @Column("organization_id") val organizationId: String,
    @Column("user_id") val userId:String,
    @Column("service_id") val serviceId: String
) {
    @Id @Transient val _id: OrganizationServicePK = OrganizationServicePK(organizationId, userId, serviceId)
    companion object {
        data class OrganizationServicePK(
            val organizationId: String,
            val userId: String,
            val serviceId: String
        )
    }
}