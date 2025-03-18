package com.gcgenome.rms.data

import com.gcgenome.rms.tables.references.*
import org.jooq.Table
import org.jooq.impl.DSL.name
import java.util.*

object TableRegistry {
    private val tableMap: Map<String, Table<*>> = mapOf(
        "AUDIT" to AUDIT,
        "CATEGORY" to CATEGORY,
        "COMMENT" to COMMENT,
        "EXTENSION" to EXTENSION,
        "ORGANIZATION" to ORGANIZATION,
        "PATIENT" to PATIENT,
        "POST" to POST,
        "POST_CATEGORY" to POST_CATEGORY,
        "POST_FILE" to POST_FILE,
        "POST_READ" to POST_READ,
        "REPORT" to REPORT,
        "REQUEST" to REQUEST,
        "REQUEST_GROUP" to REQUEST_GROUP,
        "REQUEST_RELATION" to REQUEST_RELATION,
        "SAMPLE" to SAMPLE,
        "SAMPLE_EXTENSION" to SAMPLE_EXTENSION,
        "SAMPLE_TYPE" to SAMPLE_TYPE,
        "SERVICE" to SERVICE,
        "SERVICE_EXTENSION" to SERVICE_EXTENSION,
        "SERVICE_SAMPLE_TYPE" to SERVICE_SAMPLE_TYPE,
        "USER" to USER,
        "USER_HISTORY" to USER_HISTORY,
        "USER_SERVICE" to USER_SERVICE
    )
    fun convertTypeValue(tableName: String, columnName: String, value: String): Any? {
        val table = tableMap[tableName.uppercase()] ?: return null
        val field = table.field(name(columnName)) ?: return null
        
        val conValue = when (field.dataType.type) {
            UUID::class.java -> UUID.fromString(value) ?: value
            Int::class.java -> value.toIntOrNull() ?: value
            Long::class.java -> value.toLongOrNull() ?: value
            Double::class.java -> value.toDoubleOrNull() ?: value
            Boolean::class.java -> value.toBooleanStrictOrNull() ?: value
            else -> value
        }
        return conValue
    }
}