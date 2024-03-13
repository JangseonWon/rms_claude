package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.records.OrderRecord
import com.gcgenome.rms.tables.references.ORDER
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*


interface OrderDao {

    fun DSLContext.insertOrder(userId: String): Mono<OrderRecord> {
        return Mono.from(
            insertInto(ORDER)
                .set(ORDER.ID, UUID.randomUUID())
                .set(ORDER.USER_ID, userId)
                .returning()
        )
    }
}
