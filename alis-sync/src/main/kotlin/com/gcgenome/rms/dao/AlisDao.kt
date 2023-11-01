package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisOrder
import com.gcgenome.rms.data.AlisOrderFile
import com.gcgenome.rms.tables.references.ALIS_ORDER_FILE
import com.gcgenome.rms.tables.references.ALIS_ORDER_MV
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime


interface AlisDao {
    fun DSLContext.selectAlisOrder(page: Int, limit: Int, dateFrom: String, dateTo: String): Flux<AlisOrder> {
        return Flux.from(
            selectFrom(ALIS_ORDER_MV)
                .where(ALIS_ORDER_MV.ORDER_DATE.between(LocalDateTime.parse(dateFrom)).and(LocalDateTime.parse(dateTo)))
                .orderBy(ALIS_ORDER_MV.ORDER_DATE.asc(),ALIS_ORDER_MV.ORDER_NUMBER.asc(), ALIS_ORDER_MV.SERVICE_CODE.asc())
                .limit(limit)
                .offset(page*limit)
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

    fun DSLContext.selectAlisOrderFile(page: Int, limit: Int, dateFrom: String, dateTo: String): Flux<AlisOrderFile>{
        return Flux.from(
            selectFrom(ALIS_ORDER_FILE)
                .where(ALIS_ORDER_FILE.CREATE_AT.between(LocalDateTime.parse(dateFrom)).and(LocalDateTime.parse(dateTo))).and(
                    ALIS_ORDER_FILE.ORDER_NUMBER.eq(1715031))
                .orderBy(ALIS_ORDER_FILE.CREATE_AT.asc(), ALIS_ORDER_FILE.ORDER_NUMBER.asc(), ALIS_ORDER_FILE.SERVICE_CODE.asc())
                .limit(limit)
                .offset(page*limit)
        ).map { it.into(AlisOrderFile::class.java) }
    }

    fun DSLContext.selectAlisOrderFileCount(fromDate: String, toDate: String): Mono<Int> {
        return Mono.from(select(count(ALIS_ORDER_FILE.ORDER_NUMBER).`as`("count")).from(ALIS_ORDER_FILE)
            .where(ALIS_ORDER_FILE.CREATE_AT.between(LocalDateTime.parse(fromDate)).and(LocalDateTime.parse(toDate)))
        ).map { r -> r.getValue("count", Int::class.java) }
    }
}