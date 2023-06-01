package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.UUID

@Schema("rms")
@Table(name = "sample_extension", schema = "rms")
data class SampleExtension(
    @Column("sample_id") val sampleId: UUID,
    @Column("extension_id") val extensionId: String,
    @Column("value") val value: String
){
    @Id
    @Transient
    val _id: SampleExtensionPK = SampleExtensionPK(sampleId, extensionId)
    companion object {
        data class SampleExtensionPK(
            val sampleId: UUID,
            val extensionId: String
        )
    }
}
