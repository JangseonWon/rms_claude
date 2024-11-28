package com.gcgenome.rms.dao

import com.gcgenome.rms.data.ServiceDTO
import com.gcgenome.rms.tables.pojos.Service
import com.gcgenome.rms.tables.records.ServiceRecord
import com.gcgenome.rms.tables.references.*
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ServiceDao  {
    fun DSLContext.selectServiceByUserIdAndCategoryId(andWhere: Condition, categoryId: UUID): Flux<ServiceDTO> {
        return Flux.from(
            selectDistinct(
                SERVICE.ID,
                SERVICE.NAME,
                SERVICE.GROUP_NAME,
                SERVICE.TYPE
            ).from(SERVICE)
                .leftJoin(USER_SERVICE).on(SERVICE.ID.eq(USER_SERVICE.SERVICE_ID))
                .where(SERVICE.CATEGORY_ID.eq(categoryId).and(andWhere))
        ).map { it.into(ServiceDTO::class.java) }
    }

    fun DSLContext.checkServiceId(serviceId: String): Mono<ServiceRecord> {
        return Mono.from(
            selectFrom(SERVICE).where(SERVICE.ID.eq(serviceId))
        )
    }

    fun DSLContext.selectServiceById(serviceId: String): Mono<Service> {
        return Mono.from( selectFrom(SERVICE).where(SERVICE.ID.eq(serviceId))).map { it.into(Service::class.java) }
    }

    fun DSLContext.selectServiceForGroupServiceId(groupName: String): Flux<Service> {
        return Flux.from(selectFrom(SERVICE).where(SERVICE.GROUP_NAME.eq(groupName)).orderBy(SERVICE.ID)).map { it.into(
            Service::class.java) }
    }

    fun DSLContext.selectServiceInfoById(serviceId: String): Mono<ServiceDTO>{
        return Mono.from(
            select(
                SERVICE.ID.`as`("id"),
                SERVICE.NAME.`as`("name"),
                SERVICE.NAME_KR.`as`("name_kr"),
                SERVICE.GROUP_NAME.`as`("group_name"),
                SERVICE.TYPE.`as`("type"),
                `when`(
                    CATEGORY.ID.isNotNull,
                    jsonObject(
                        key("id").value(CATEGORY.ID),
                        key("name").value(CATEGORY.NAME),
                        key("order_type").value(CATEGORY.ORDER_TYPE)
                    )
                ).`as`("category"),
                `when`(
                    count(SAMPLE_TYPE.ID).greaterThan(0),
                    jsonArrayAggDistinct(
                        jsonbObject(
                            key("id").value(SAMPLE_TYPE.ID),
                            key("name").value(SAMPLE_TYPE.NAME)
                        )
                    )
                ).`as`("sample_types"),
                `when`(
                    count(EXTENSION.ID).greaterThan(0),
                    jsonArrayAggDistinct(
                        jsonbObject(
                            key("id").value(EXTENSION.ID),
                            key("name").value(EXTENSION.NAME),
                            key("required").value(SERVICE_EXTENSION.REQUIRED),
                            key("regex").value(EXTENSION.REGEX)
                        )
                    )
                ).`as`("extensions")
            ).from(SERVICE)
                .leftJoin(CATEGORY).on(SERVICE.CATEGORY_ID.eq(CATEGORY.ID))
                .leftJoin(SERVICE_SAMPLE_TYPE).on(SERVICE.ID.eq(SERVICE_SAMPLE_TYPE.SERVICE_ID))
                .leftJoin(SAMPLE_TYPE).on(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID))
                .leftJoin(SERVICE_EXTENSION).on(SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID))
                .leftJoin(EXTENSION).on(SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID))
                .where(SERVICE.ID.eq(serviceId))
                .groupBy(SERVICE.ID, CATEGORY.ID)
        ).map { it.into(ServiceDTO::class.java) }
    }
}