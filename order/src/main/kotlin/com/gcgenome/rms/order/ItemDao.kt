package com.gcgenome.rms.order

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

    private fun map(userId: String, orderId: UUID, dto: Item_) = Item(
        _id = UUID.randomUUID(),
        serviceId = dto.service,
        orderId = orderId,
        patientSerial = dto.patient?.serial,
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
