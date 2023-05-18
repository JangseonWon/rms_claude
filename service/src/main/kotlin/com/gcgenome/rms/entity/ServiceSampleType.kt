package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Schema("rms")
@Table("service_sample_type")
data class ServiceSampleType(
    @Column("service_id") val serviceId: String,
    @Column("sample_type_id") val sampleTypeId: String
) {
    @Id
    @Transient
    val _id: ServiceSampleTypePK = ServiceSampleTypePK(serviceId, sampleTypeId)
    companion object {
        data class ServiceSampleTypePK(
            val serviceId: String,
            val sampleTypeId: String
        )
    }
}
