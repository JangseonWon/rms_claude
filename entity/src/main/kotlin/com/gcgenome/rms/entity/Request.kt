package com.gcgenome.rms.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(schema = "rms_dev", name = "request")
data class Request(
    @EmbeddedId
    val pk: RequestPK,
    @Column(name = "user_service_id", length = 64, nullable = false)
    val userServiceId: String,
    @Column(name = "status", length = 64, nullable = false)
    val status: String,
    @Column(name = "memo", nullable = true)
    val memo: String,
    @Column(name = "department", length = 64, nullable = true)
    val department: String,
    @Column(name = "ward", length = 64, nullable = true)
    val ward: String,
    @Column(name = "physician", length = 64, nullable = true)
    val physician: String,
    @Column(name = "create_at", nullable = true)
    val createAt: LocalDateTime,
    @Column(name = "cart_at", nullable = true)
    val cartAt: LocalDateTime,
    @Column(name = "specified_at", nullable = true)
    val specifiedAt: LocalDateTime,
    @Column(name = "complete_at", nullable = true)
    val completeAt: LocalDateTime,
    @Column(name = "resample_at", nullable = true)
    val resampleAt: LocalDateTime,
    @Column(name = "last_modify_at", nullable = false)
    val lastModifyAt: LocalDateTime,
    @Column(name = "emp_id", length = 64, nullable = true)
    val empId: String,
    @Column(name = "emp_name", length = 64, nullable = true)
    val empName: String,
    @Column(name = "emp_mobile", length = 64, nullable = true)
    val empMobile: String,
    @Column(name = "test", nullable = true)
    val test: Boolean,
    @Column(name = "credit", nullable = true)
    val credit: Boolean,
    @Column(name = "price", nullable = true)
    val price: Int,
    @Column(name = "outsourcing_cost", nullable = true)
    val outsourcingCost: Int,

    @OneToMany(mappedBy = "requestId")
    val report: List<Report>,

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    val orderId: Order,
    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    val serviceId: Service,
    @ManyToOne
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    val sampleId: Sample
){
    @Embeddable
    data class RequestPK (
        @Column(name = "order_id") val orderId: UUID,
        @Column(name = "service_id") val serviceId: String,
        @Column(name = "sample_id") val sampleId: UUID
    ) : Serializable
}