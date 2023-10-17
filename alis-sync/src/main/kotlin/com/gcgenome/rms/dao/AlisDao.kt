package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisOrder
import com.gcgenome.rms.tables.references.ALIS_ORDER
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime


interface AlisDao {
    fun DSLContext.selectAlisOrderTest(page: Int, count: Int): Flux<AlisOrder> {
        return Flux.from(
            selectFrom(ALIS_ORDER)
                .where(ALIS_ORDER.ORDER_DATE.eq(LocalDateTime.parse("2023-10-02T00:00:00")))
                .orderBy(ALIS_ORDER.ORDER_NUMBER.asc())
                .limit(count)
                .offset(page*count)
        ).map { it.into(AlisOrder::class.java) }
    }

    fun DSLContext.selectAlisOrder(): Flux<AlisOrder> {
        return Flux.from(selectFrom(ALIS_ORDER)).map { it.into(AlisOrder::class.java) }
    }

    fun DSLContext.selectAlisOrderCount(): Mono<Int> {
        return Mono.from(select(count(ALIS_ORDER.ORDER_NUMBER).`as`("count")).from(ALIS_ORDER)
            .where(ALIS_ORDER.ORDER_DATE.eq(LocalDateTime.parse("2023-10-02T00:00:00")))
        ).map { r -> r.getValue("count", Int::class.java) }
    }
}