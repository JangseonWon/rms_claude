package com.gcgenome.rms.order

import com.gcgenome.rms.data.CancelOrder_
import com.gcgenome.rms.data.Item_
import com.gcgenome.rms.entity.Item
import com.gcgenome.rms.repo.ItemRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.ItemDao")
class ItemDao(
    val itemRepo: ItemRepository,
) {
    fun saveItem(userId:String, orderId: UUID, dto: Item_): Mono<Item_> = itemRepo.save(map(userId, orderId, dto)).map { entity->map(dto, entity) }

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

    private fun map(userId: String, orderId: UUID, dto: Item_) = Item(
        _id = UUID.randomUUID(),
        serviceId = dto.service,
        orderId = orderId,
        patientSerial = dto.patient.serial,
        organizationId = userId,
        userId = userId,
    )
    private fun map(dto: Item_, entity: Item): Item_ = Item_(
        service = entity.serviceId!!,
        orderAt = entity.orderAt.toString(),
        patient = dto.patient
    ).apply {
        id = entity._id
    }
}
