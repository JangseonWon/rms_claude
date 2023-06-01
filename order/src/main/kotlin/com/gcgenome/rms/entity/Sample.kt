package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.UUID

@Schema("rms")
@Table(name = "sample", schema = "rms")
data class Sample(
    @Id @Column("id") val _id:UUID,
    @Column("sample_type_id") val sampleTypeId: String,
    @Column("patient_serial") val patientSerial: String,
    @Column("organization_id") val organizationId: String,
    @Column("user_id") val userId: String,
    @Column("serial") val serial: String?,
    @Column("age") val age: Int?,
    @Column("sampling") val sampling: LocalDate,
    @Column("note") val note: String?,
    @Column("state") val state: String?,
    @Column("department") val department: String?,
    @Column("ward") val ward: String?,
    @Column("physician") val physician: String?
):Persistable<UUID>{
    @CreatedDate
    @Column("registration_at") lateinit var registrationAt: LocalDate
    override fun getId(): UUID = _id
    override fun isNew(): Boolean = true
}
