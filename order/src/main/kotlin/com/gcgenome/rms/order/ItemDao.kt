package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.ITEM
import com.gcgenome.rms.data.Item_
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

interface ItemDao {
    fun DSLContext.insertItem(userId: String, orderId: UUID, item:Item_) =
        insertInto(ITEM)
        .columns(ITEM.ID, ITEM.ORDER_AT, ITEM.ORDER_ID, ITEM.ORGANIZATION_ID, ITEM.PATIENT_SERIAL, ITEM.USER_ID, ITEM.SERVICE_ID)
        .values(UUID.randomUUID(), LocalDateTime.now(), orderId, userId, item.patient.serial, userId, item.service)
        .returning()


}
