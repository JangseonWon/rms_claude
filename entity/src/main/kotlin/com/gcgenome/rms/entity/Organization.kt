package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(schema = "public", name = "organization")
data class Organization(
    @EmbeddedId
    val pk: OrganizationPK,
    @Column(name = "name", length = 64)
    val name: String = "",
    @Column(name = "registration_number", length = 64)
    val registrationNumber: String = "",
    @Column(name = "type", length = 64)
    val type: String = "",
    @Column(name = "nursing_number", length = 64)
    val nursingNumber: String = "",
    @Column(name = "branch_id", length = 64)
    val branchId: String = "",
    @Column(name = "branch_name", length = 64)
    val branchName: String = "",
    @OneToMany(mappedBy = "organizationId")
    val patient: List<Patient>
){
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    lateinit var userId: User

    companion object {
        @Embeddable
        data class OrganizationPK(
            @Column(name = "id") val id: String,
            @Column(name = "user_id") val userId: String
        ) : Serializable
    }
}
