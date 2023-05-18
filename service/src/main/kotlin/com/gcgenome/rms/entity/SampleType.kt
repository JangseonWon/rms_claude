package com.gcgenome.rms.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Schema("rms")
@Table("sample_type")
data class SampleType(
    @Id @Column("id") val id: String,
    @Column("name") val name: String,
)