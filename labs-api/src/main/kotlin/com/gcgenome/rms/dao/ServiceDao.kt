package com.gcgenome.rms.dao

import com.gcgenome.rms.service.dto.request.ServicePostDTO
import com.gcgenome.rms.service.dto.response.ServiceResponseDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import java.util.*

interface ServiceDao {
    fun DSLContext.searchServices(userId: UUID, service: ServicePostDTO): Flux<ServiceResponseDTO> {
        return Flux.from(
            select(
                SERVICE.CODE,
                USER_SERVICE.SERIAL,
                SERVICE.NAME_KR,
                SERVICE.NAME_ER,
                DSL.jsonArrayAggDistinct(
                    DSL.jsonbObject(
                        DSL.key("code").value(SAMPLE_TYPE.CODE),
                        DSL.key("serial").value(USER_SAMPLE_TYPE.SERIAL),
                        DSL.key("name_kr").value(SAMPLE_TYPE.NAME_KR),
                        DSL.key("name_en").value(SAMPLE_TYPE.NAME_EN)
                    )
                ).filterWhere(SAMPLE_TYPE.CODE.isNotNull)
                    .`as`("sample_types"),
                DSL.jsonArrayAggDistinct(
                    DSL.jsonbObject(
                        DSL.key("code").value(EXTENSION.CODE),
                        DSL.key("name_kr").value(EXTENSION.NAME_KR),
                        DSL.key("name_en").value(EXTENSION.NAME_EN),
                        DSL.key("is_required").value(SERVICE_EXTENSION.IS_REQUIRED),
                        DSL.key("regex").value(EXTENSION.REGEX)
                    )
                ).filterWhere(EXTENSION.CODE.isNotNull)
                    .`as`("extensions")
            ).from(SERVICE)
                .join(USER_SERVICE).on(
                    USER_SERVICE.SERVICE_ID.eq(SERVICE.ID),
                    USER_SERVICE.USER_ID.eq(userId)
                )
                .join(SERVICE_SAMPLE_TYPE).on(SERVICE_SAMPLE_TYPE.SERVICE_ID.eq(SERVICE.ID))
                .join(SAMPLE_TYPE).on(SAMPLE_TYPE.ID.eq(SERVICE_SAMPLE_TYPE.SAMPLE_TYPE_ID))
                .join(USER_SAMPLE_TYPE).on(
                    USER_SAMPLE_TYPE.SAMPLE_TYPE_ID.eq(SAMPLE_TYPE.ID),
                    USER_SAMPLE_TYPE.USER_ID.eq(userId)
                )
                .leftJoin(SERVICE_EXTENSION).on(SERVICE_EXTENSION.SERVICE_ID.eq(SERVICE.ID))
                .leftJoin(EXTENSION).on(EXTENSION.ID.eq(SERVICE_EXTENSION.EXTENSION_ID))
                .where(
                    listOfNotNull(
                        service.code?.takeIf { it.isNotBlank() }?.let { SERVICE.CODE.like("%$it%") },
                        service.serial?.takeIf { it.isNotBlank() }?.let { USER_SERVICE.SERIAL.like("%$it%") },
                        service.nameKr?.takeIf { it.isNotBlank() }?.let { SERVICE.NAME_KR.like("%$it%") },
                        service.nameEn?.takeIf { it.isNotBlank() }?.let { SERVICE.NAME_ER.like("%$it%") }
                    )
                )
                .groupBy(
                    SERVICE.ID,
                    USER_SERVICE.SERIAL
                )
        ).map { it.into(ServiceResponseDTO::class.java) }
    }
}