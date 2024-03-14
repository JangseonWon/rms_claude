package com.gcgenome.rms.cart

import com.gcgenome.rms.dao.ItemDao
import com.gcgenome.rms.data.Item
import com.gcgenome.rms.data.Status
import com.gcgenome.rms.exceptions.ItemNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class CartHandler( val dslContext: DSLContext ): ItemDao {

    fun getCartInfo(orderId: UUID, sampleId: UUID): Mono<Item> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
             selectItemBySampleIdAndStatus(orderId, sampleId, Status.CART)
                .switchIfEmpty(Mono.error(ItemNotFoundException(sampleId.toString())))
            }
        })
    }
}