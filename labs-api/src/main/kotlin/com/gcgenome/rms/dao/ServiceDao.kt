package com.gcgenome.rms.dao

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gcgenome.rms.service.dto.request.ServicePostDTO
import com.gcgenome.rms.service.dto.response.ExtensionResponseDTO
import com.gcgenome.rms.service.dto.response.SampleTypeServiceResponseDTO
import com.gcgenome.rms.service.dto.response.ServiceTypeResponseDTO
import com.gcgenome.rms.tables.references.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import java.util.*

private val MAPPER = jacksonObjectMapper()
private val NIL_UUID: UUID = UUID(0L, 0L)

interface ServiceDao {
    fun DSLContext.searchServices(userId: UUID, service: ServicePostDTO): Flux<ServiceTypeResponseDTO> {
        return Flux.from(
            select(
                SERVICE.CODE,
                USER_SERVICE.SERIAL,
                SERVICE.NAME_KR,
                SERVICE.NAME_ER,
                DSL.jsonArrayAggDistinct(
                    DSL.jsonbObject(
                        DSL.key("id").value(SAMPLE_TYPE.ID),
                        DSL.key("code").value(SAMPLE_TYPE.CODE),
                        DSL.key("serial").value(USER_SAMPLE_TYPE.SERIAL),
                        DSL.key("name_kr").value(SAMPLE_TYPE.NAME_KR),
                        DSL.key("name_en").value(SAMPLE_TYPE.NAME_EN)
                    )
                ).filterWhere(SAMPLE_TYPE.CODE.isNotNull)
                    .`as`("sample_types"),
                DSL.jsonArrayAggDistinct(
                    DSL.jsonbObject(
                        DSL.key("id").value(EXTENSION.ID),
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
        ).map { rec ->
            val code   = rec.get(SERVICE.CODE) ?: ""
            val serial = rec.get(USER_SERVICE.SERIAL)
            val nameKr = rec.get(SERVICE.NAME_KR)
            val nameEn = rec.get(SERVICE.NAME_ER)

            val sampleTypesStr = rec.get("sample_types", String::class.java)
            val extensionsStr  = rec.get("extensions",  String::class.java)

            val sampleTypes: List<SampleTypeServiceResponseDTO> =
                parseSampleTypes(sampleTypesStr)

            val extensions: List<ExtensionResponseDTO> =
                parseExtensions(extensionsStr)

            ServiceTypeResponseDTO(
                code = code,
                serial = serial,
                nameKr = nameKr,
                nameEn = nameEn,
                sampleTypes = sampleTypes,
                extensions = extensions
            )
        }
    }
    private fun parseSampleTypes(json: String?): List<SampleTypeServiceResponseDTO> {
        if (json.isNullOrBlank()) return emptyList()
        val root: JsonNode = MAPPER.readTree(json)
        if (!root.isArray) return emptyList()
        return root.map { n ->
            val idText = n.path("id").asText(null)
            val id = idText?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: NIL_UUID
            SampleTypeServiceResponseDTO(
                id     = id,
                code   = n.path("code").asText(""),
                serial = n.path("serial").asText(""),
                nameKr = n.path("name_kr").takeIf { !it.isNull }?.asText(),
                nameEn = n.path("name_en").takeIf { !it.isNull }?.asText()
            )
        }
    }

    private fun parseExtensions(json: String?): List<ExtensionResponseDTO> {
        if (json.isNullOrBlank()) return emptyList()
        val root: JsonNode = MAPPER.readTree(json)
        if (!root.isArray) return emptyList()
        return root.map { n ->
            val idText = n.path("id").asText(null)
            val id = idText?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: NIL_UUID
            ExtensionResponseDTO(
                id         = id,
                code       = n.path("code").asText(""),
                nameKr     = n.path("name_kr").takeIf { !it.isNull }?.asText(),
                nameEn     = n.path("name_en").takeIf { !it.isNull }?.asText(),
                isRequired = n.path("is_required").asBoolean(false),
                regex      = n.path("regex").takeIf { !it.isNull }?.asText()
            )
        }
    }

}