package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.ITEM
import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Item_
import com.gcgenome.rms.entity.Item
import com.gcgenome.rms.repo.ItemRepository
import org.jooq.Configuration
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Repository("com.gcgenome.rms.order.ItemDao")
class ItemDao(
    val itemRepo: ItemRepository,
) {
    fun insertItem(trx: Configuration, userId: String, orderId: UUID, item:Item_) = trx.dsl()
        .insertInto(ITEM)
        .columns(ITEM.ID, ITEM.ORDER_AT, ITEM.ORDER_ID, ITEM.ORGANIZATION_ID, ITEM.PATIENT_SERIAL, ITEM.USER_ID, ITEM.SERVICE_ID)
        .values(UUID.randomUUID(), LocalDateTime.now(), orderId, userId, item.patient.serial, userId, item.service)
        .returning()

    fun deleteItem(sampleId: UUID, itemId: UUID): Mono<CancelOrder_> =
        itemRepo.findById(itemId).flatMap { item -> itemRepo.delete(item)
            .then(Mono.just(CancelOrder_(sampleId, "의뢰 취소 되었습니다.")
                .apply { this.itemId = itemId; })) }

    fun findItem(itemId: UUID): Mono<Item> {
        return itemRepo.findById(itemId)
    }

    fun countItem(itemId: UUID): Mono<Long> {
        return itemRepo.findById(itemId)
            .flatMap { itemRepo.countItemByOrderId(it.orderId) }
    }

    fun countMrn(mrn: String): Mono<Long> {
        return itemRepo.countByPatientSerial(mrn)
    }
}
