package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(schema = "rms_dev2", name = "organization")
data class Organization(
    @EmbeddedId
    val pk: OrganizationPK,
    @Column(name = "name", length = 64, nullable = false)
    val name: String,
    @Column(name = "registration_number", length = 64, nullable = true)
    val registrationNumber: String,
    @Column(name = "type", length = 64, nullable = true)
    val type: String,
    @Column(name = "nursing_number", length = 64, nullable = true)
    val nursingNumber: String,

    @OneToMany(mappedBy = "organizationId")
    val patient: List<Patient>,

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    val userId: User
){
    @Embeddable
    data class OrganizationPK(
        @Column(name = "id") val id: String,
        @Column(name = "user_id") val userId: String
    ) : Serializable
}
