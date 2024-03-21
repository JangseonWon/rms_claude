package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(schema = "rms_dev2", name = "audit")
data class Audit(
    @EmbeddedId
    val pk: AuditPK,
    @Column(name = "url", length = 64)
    val url: String,
    @Column(name = "method", length = 8)
    val method: String,
    @Column(columnDefinition = "jsonb")
    val value: String,
){
    @Embeddable
    data class AuditPK(
        @Column(name = "create_at") val createAt: LocalDateTime,
        @Column(name = "create_by", length=128) val createBy: String
    ) : Serializable
}