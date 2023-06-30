package com.gcgenome.rms.data

import com.gcgenome.lims.tables.records.OrganizationServiceRecord

data class OrganizationService (
    val organizationId: String?,
    val serviceId: String?,
    val userId: String?
) {
    companion object {
        fun toModel(record: OrganizationServiceRecord) =
            OrganizationService(
                organizationId = record.getValue("organization_id", String::class.java),
                serviceId = record.getValue("service_id", String::class.java),
                userId = record.getValue("user_id", String::class.java)
            )
    }
}