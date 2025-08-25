package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    schema = "labs_dev",
    name = "extension",
    uniqueConstraints = [UniqueConstraint(name = "uk_extension_code", columnNames = ["code"])]
)
data class Extension(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "code", length = 64, nullable = false)
    val code: String,
    @Column(name = "name_kr", length = 64, nullable = true)
    val nameKr: String,
    @Column(name = "name_en", length = 64, nullable = true)
    val nameEn: String,
    @Column(name = "regex", length = 64, nullable = true)
    val type: String,

    @OneToMany(mappedBy = "extensionId")
    val serviceExtensions: List<ServiceExtension>,
    @OneToMany(mappedBy = "extensionId")
    val requestExtensions: List<RequestExtension>,
)
