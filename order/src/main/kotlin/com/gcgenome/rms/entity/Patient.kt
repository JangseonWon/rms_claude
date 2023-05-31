package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Schema("rms")
@Table("patient")
data class Patient(
    @Column("serial") val serial: String,
    @Column("organization_id") val organizationId: String,
    @Column("user_id") val userId: String,
    @Column("sex") val sex: String,
    @Column("name") val name: String,
    @Column("birth_year") val birthYear: Int?,
    @Column("birth_month") val birthMonth: Int?,
    @Column("birth_day") val birthDay: Int?,
): Persistable<Patient.Companion.PatientPK>{
    @Id
    @Transient
    val _id: PatientPK = PatientPK(serial, organizationId, userId)
    override fun getId(): PatientPK = _id
    override fun isNew(): Boolean = true

    companion object {
        data class PatientPK(
            val serial: String,
            val organizationIdId: String,
            val userId: String
        )
    }
}
