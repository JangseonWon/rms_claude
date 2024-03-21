package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(schema = "rms_dev2", name = "service_sample_type")
data class ServiceSampleType(
    @EmbeddedId
    val pk: ServiceSampleTypePK,

    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    val serviceId: Service,

    @ManyToOne
    @JoinColumn(name = "sample_type_id", insertable = false, updatable = false)
    val sampleTypeId: SampleType

){
    @Embeddable
    data class ServiceSampleTypePK(
        @Column(name = "service_id") val serviceId: String,
        @Column(name = "sample_type_id") val sampleTypeId: String
    ) : Serializable
}
