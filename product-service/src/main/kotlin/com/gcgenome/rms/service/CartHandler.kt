package com.gcgenome.rms.service

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Status
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CartHandler(
    val dslContext: DSLContext,
    val orderHandler: OrderHandler
): PatientDao, OrderDao, OrganizationDao, ItemDao, ExtensionDao, SampleDao, UserDao, UserServiceDao, ServiceSampleTypeDao {

    fun insertCartRequest(userId: String, dto: List<Item>): Mono<List<Item>> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromIterable(dto).concatMap { items ->
                    insertOrder(userId).flatMap { orderRecord ->
                        orderHandler.checkUserServiceById(userId, items.serviceId, trx)
                            .then(orderHandler.checkOrganization(userId, items.patient.organization.id, trx))
                            .then(insertPatient(items.patient.organization.id, userId, items.patient))
                            .then(insertItem(orderRecord.id!!, items.serviceId, items.serial))
                            .thenMany(orderHandler.insertSampleProcess(userId, orderRecord, items, Status.CART, trx))
                            .then(selectItemById(orderRecord.id!!, userId))
                    } }.collectList()
            } })
    }
}