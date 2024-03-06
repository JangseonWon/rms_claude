package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "sample_extension")
data class SampleExtension(
    @EmbeddedId
    val pk: SampleExtensionPK,
    @Column(name = "value", length = 64, nullable = false)
    val value: String,

    @ManyToOne
    @JoinColumn(name = "sample_id", insertable = false, updatable = false, nullable = false)
    val sampleId: Sample,

    @ManyToOne
    @JoinColumn(name = "extension_id", insertable = false, updatable = false, nullable = false)
    val extensionId: Extension
){
    @Embeddable
    data class SampleExtensionPK(
        @Column(name = "sample_id") val sampleId: UUID,
        @Column(name = "extension_id") val extensionId: String
    ) : Serializable
}
