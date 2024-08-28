package com.gcgenome.rms.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ProgCompMngCode", schema = "dbo")
data class ProgCompMngCode(
    @Id
    @Column(name = "CompMngID")
    val compMngId: String,
    @Column(name = "CompMngCode")
    val compMngCode: String,
    @Column(name = "CompMngBeginNo")
    val compMngBeginNo: String,
)
