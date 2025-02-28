package com.gcgenome.rms.dao

import com.gcgenome.rms.data.AlisService
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.ServiceDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


interface ServiceDao : QueryDao{
    fun DSLContext.selectServices(): Flux<ServiceDTO> {
        return Flux.from(
            selectFrom(SERVICE).orderBy(SERVICE.NAME_KR)
        ).map { it.into(ServiceDTO::class.java) }
    }

    fun DSLContext.selectServicesWithPage(query: Query): Mono<Page<ServiceDTO>> {
        val joins = listOf(
            QueryDao.JoinInfo(CATEGORY, SERVICE.CATEGORY_ID.eq(CATEGORY.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SERVICE_SAMPLE_TYPE, SERVICE.ID.eq(SERVICE_SAMPLE_TYPE.SERVICE_ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SAMPLE_TYPE, SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(SERVICE_EXTENSION, SERVICE.ID.eq(SERVICE_EXTENSION.SERVICE_ID), QueryDao.JoinType.LEFT),
            QueryDao.JoinInfo(EXTENSION, SERVICE_EXTENSION.EXTENSION_ID.eq(EXTENSION.ID), QueryDao.JoinType.LEFT)
        )
        val fields = listOf(
            SERVICE.ID.`as`("id"),
            SERVICE.NAME.`as`("name"),
            SERVICE.NAME_KR.`as`("name_kr"),
            SERVICE.GROUP_NAME.`as`("group_name"),
            SERVICE.TYPE.`as`("type"),
            `when`(CATEGORY.ID.isNotNull,
                jsonObject(
                    key("id").value(CATEGORY.ID),
                    key("name").value(CATEGORY.NAME),
                    key("order_type").value(CATEGORY.ORDER_TYPE)
                )
            ).`as`("category"),
            `when`(count(SAMPLE_TYPE.ID).greaterThan(0),
                jsonArrayAggDistinct(
                    jsonbObject(
                        key("id").value(SAMPLE_TYPE.ID),
                        key("name").value(SAMPLE_TYPE.NAME)
                    )
                )
            ).`as`("sample_types"),
            `when`(count(EXTENSION.ID).greaterThan(0),
                jsonArrayAggDistinct(
                    jsonbObject(
                        key("id").value(EXTENSION.ID),
                        key("name").value(EXTENSION.NAME),
                        key("required").value(SERVICE_EXTENSION.REQUIRED),
                        key("regex").value(EXTENSION.REGEX)
                    )
                )
            ).`as`("extensions")
        )
        val groupByFields = listOf(SERVICE.ID, CATEGORY.ID)
        return selectPage(mainTable = SERVICE, query = query, joinTables = joins, selectFields = fields, groupByFields = groupByFields) {record ->
            record.into(ServiceDTO::class.java)
        }
    }

    fun DSLContext.selectServiceById(serviceId: String): Mono<ServiceDTO>{
        return Mono.from(
            select(
                SERVICE.ID.`as`("id"),
                SERVICE.NAME.`as`("name"),
                SERVICE.NAME_KR.`as`("name_kr"),
                SERVICE.GROUP_NAME.`as`("group_name"),
                SERVICE.TYPE.`as`("type"),
                `when`(CATEGORY.ID.isNotNull,
                    jsonObject(
                        key("id").value(CATEGORY.ID),
                        key("name").value(CATEGORY.NAME),
                        key("order_type").value(CATEGORY.ORDER_TYPE)
                    )
                ).`as`("category"),
                `when`(count(SAMPLE_TYPE.ID).greaterThan(0),
                    jsonArrayAggDistinct(
                        jsonbObject(
                            key("id").value(SAMPLE_TYPE.ID),
                            key("name").value(SAMPLE_TYPE.NAME)
                        )
                    )
                ).`as`("sample_types"),
                `when`(count(EXTENSION.ID).greaterThan(0),
                    jsonArrayAggDistinct(
                        jsonbObject(
                            key("id").value(EXTENSION.ID),
                            key("name").value(EXTENSION.NAME),
                            key("required").value(SERVICE_EXTENSION.REQUIRED),
                            key("regex").value(EXTENSION.REGEX),
                            key("sort_extension").value(SERVICE_EXTENSION.SORT_EXTENSION)
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

    fun DSLContext.updateServiceById(service: ServiceDTO): Mono<ServiceDTO> {
        return Mono.from(
            update(SERVICE)
                .set(SERVICE.CATEGORY_ID, service.category?.id)
                .set(SERVICE.NAME, service.name)
                .set(SERVICE.GROUP_NAME, service.groupName)
                .set(SERVICE.TYPE, service.type)
                .where(SERVICE.ID.eq(service.id))
                .returning()
        ).map { it.into(ServiceDTO::class.java) }
    }

    fun DSLContext.cancelCategoryByService(categoryId: UUID): Mono<ServiceDTO> {
        return Mono.from(
            update(SERVICE)
                .set(SERVICE.CATEGORY_ID, `val`(null, SERVICE.CATEGORY_ID.dataType))
                .where(SERVICE.CATEGORY_ID.eq(categoryId))
                .returning()
        ).map { it.into(ServiceDTO::class.java)}
    }
    fun DSLContext.upsertService(alisService: AlisService): Mono<Int> {
        return Mono.from(
            insertInto(SERVICE)
                .set(SERVICE.ID, alisService.testCode)
                .set(SERVICE.NAME_KR, alisService.testDisplayName)
                .onConflict(SERVICE.ID)
                .doUpdate()
                .set(SERVICE.NAME_KR, alisService.testDisplayName)
        )
    }
}