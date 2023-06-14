package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.ITEM
import com.gcgenome.rms.data.Item_
import org.jooq.Configuration
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository("com.gcgenome.rms.order.ItemDao")
class ItemDao {
    fun insertItem(trx: Configuration, userId: String, orderId: UUID, item:Item_) = trx.dsl()
        .insertInto(ITEM)
        .columns(ITEM.ID, ITEM.ORDER_AT, ITEM.ORDER_ID, ITEM.ORGANIZATION_ID, ITEM.PATIENT_SERIAL, ITEM.USER_ID, ITEM.SERVICE_ID)
        .values(UUID.randomUUID(), LocalDateTime.now(), orderId, userId, item.patient.serial, userId, item.service)
        .returning()

}
