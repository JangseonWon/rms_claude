package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(schema = "public", name = "patient")
data class Patient(
    @EmbeddedId
    val patientPK: PatientPK,
    @Column(name = "sex", length = 1)
    val sex: String,
    @Column(name = "name", length = 64)
    val name: String,
    @Column(name = "birth_year", columnDefinition = "numeric(4)")
    val birthYear: Int?,
    @Column(name = "birth_month", columnDefinition = "numeric(2)")
    val birthMonth: Int?,
    @Column(name = "birth_day", columnDefinition = "numeric(2)")
    val birthDay: Int?,
    @OneToMany(mappedBy = "patientId")
    val item: List<Item>,
    @OneToMany(mappedBy = "patientId")
    val sample: List<Sample>

){
    @ManyToOne
    @JoinColumns(value = [
        JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false)
        , JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    ])
    lateinit var organizationId: Organization

    companion object {
        @Embeddable
        data class PatientPK(
            @Column(name = "serial") val serial: String,
            @Column(name = "organization_id") val organizationId: String,
            @Column(name = "user_id") val userId: String
        ) : Serializable
    }
}
