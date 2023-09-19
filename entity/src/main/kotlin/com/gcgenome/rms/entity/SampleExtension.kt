package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
@Table(schema = "public", name = "sample_extension")
data class SampleExtension(
    @EmbeddedId
    val pk: SampleExtensionPK,
    @Column(name = "value", length = 64)
    val value: String
){
    @ManyToOne
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    lateinit var sampleId: Sample

    @ManyToOne
    @JoinColumn(name = "extension_id", insertable = false, updatable = false)
    lateinit var extensionId: Extension

    companion object {
        @Embeddable
        data class SampleExtensionPK(
            @Column(name = "sample_id") val sampleId: UUID,
            @Column(name = "extension_id") val extensionId: String
        ) : Serializable
    }
}
