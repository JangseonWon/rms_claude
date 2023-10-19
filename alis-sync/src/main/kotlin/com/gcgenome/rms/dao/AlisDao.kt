package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisOrder
import com.gcgenome.rms.tables.references.ALIS_ORDER_MV
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime


interface AlisDao {
    fun DSLContext.selectAlisOrder(page: Int, count: Int, dateFrom: String, dateTo: String): Flux<AlisOrder> {
        return Flux.from(
            selectFrom(ALIS_ORDER_MV)
                .where(ALIS_ORDER_MV.ORDER_DATE.between(LocalDateTime.parse(dateFrom)).and(LocalDateTime.parse(dateTo)))
                .orderBy(ALIS_ORDER_MV.ORDER_DATE.asc(),ALIS_ORDER_MV.ORDER_NUMBER.asc())
                .limit(count)
                .offset(page*count)
        ).map { it.into(AlisOrder::class.java) }
    }

    fun DSLContext.selectAlisOrderCount(fromDate: String, toDate: String): Mono<Int> {
        return Mono.from(select(count(ALIS_ORDER_MV.ORDER_NUMBER).`as`("count")).from(ALIS_ORDER_MV)
            .where(ALIS_ORDER_MV.ORDER_DATE.between(LocalDateTime.parse(fromDate)).and(LocalDateTime.parse(toDate)))
        ).map { r -> r.getValue("count", Int::class.java) }
    }

    fun DSLContext.refreshAlisOrder(): Mono<Int> {
        return execute("refresh materialized view alis_order_mv").toMono()
    }
}