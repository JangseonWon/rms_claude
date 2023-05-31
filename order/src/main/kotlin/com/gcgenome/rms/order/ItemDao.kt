package com.gcgenome.rms.order

import com.gcgenome.rms.data.Items_
import com.gcgenome.rms.entity.Item
import com.gcgenome.rms.repo.ItemRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.ItemDao")
class ItemDao(
    val itemRepo: ItemRepository,
) {
    fun saveItem(orderId: UUID, dto: Items_): Mono<Items_> = itemRepo.save(map(orderId, dto)).map { entity->map(dto, entity) }

    private fun map(orderId: UUID, dto: Items_) = Item(
        _id = UUID.randomUUID(),
        serviceId = dto.service,
        orderId = orderId,
        patientSerial = null,
        organizationId = null,
        userId = null,
    )
    private fun map(dto: Items_,entity: Item): Items_ = Items_(
        service = entity.serviceId!!,
    ).apply {
        patient = dto.patient
        id = entity._id
        orderAt = entity.orderAt
    }
}
