package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Schema("rms")
@Table("organization")
data class Organization (
    @Column("id") val id: String,
    @Column("user_id") val userId: String,
    @Column("name") val name: String
){
    @Id
    @Transient
    val _id: OrganizationPK = OrganizationPK(id, userId)
    companion object {
        data class OrganizationPK(
            val id: String,
            val userId: String
        )
    }
}