package com.gcgenome.rms.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "public", name = "item")
data class Item(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "serial", length = 64)
    val serial: String,
    @ManyToOne
    @JoinColumn(name = "service_id")
    val serviceId: Service,
    @ManyToOne
    @JoinColumn(name = "order_id")
    val orderId: Order
){
    @ManyToOne
    @JoinColumns(value = [
        JoinColumn(name = "patient_serial", referencedColumnName = "serial", insertable=false, updatable=false),
        JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable=false, updatable=false),
        JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable=false, updatable=false)
    ])
    lateinit var patientId: Patient
}