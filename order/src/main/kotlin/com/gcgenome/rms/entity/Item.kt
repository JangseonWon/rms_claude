package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Schema("rms")
@Table(name = "item", schema = "rms")
data class Item(
    @Id @Column("id") val _id: UUID,
    @Column("service_id") val serviceId: String?,
    @Column("order_id") val orderId: UUID?,
    @Column("patient_serial") val patientSerial: String?,
    @Column("organization_id") val organizationId: String?,
    @Column("user_id") val userId: String?
): Persistable<UUID> {
    @CreatedDate
    @Column("order_at") lateinit var orderAt: LocalDateTime
    override fun getId(): UUID = _id
    override fun isNew(): Boolean = true

}
