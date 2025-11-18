package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Extension
import com.idrsys.ailis.rms.domain.model.ExtensionDataType
import com.idrsys.ailis.rms.domain.model.RequestExtension
import com.idrsys.ailis.rms.domain.repository.ExtensionRepository
import com.idrsys.ailis.rms.domain.repository.RequestExtensionRepository
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Extension Repository 구현체 (R2DBC)
 */
@Repository
class R2dbcExtensionRepository(
    private val databaseClient: DatabaseClient
) : ExtensionRepository {

    override suspend fun save(extension: Extension): Extension {
        return if (extension.id == null) {
            insert(extension)
        } else {
            update(extension)
        }
    }

    private suspend fun insert(extension: Extension): Extension {
        val sql = """
            INSERT INTO extensions (
                code, name, description, data_type, is_required,
                created_at, created_by
            ) VALUES (
                :code, :name, :description, :dataType, :isRequired,
                :createdAt, :createdBy
            ) RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("code", extension.code)
            .bind("name", extension.name)
            .bindNullable("description", extension.description)
            .bind("dataType", extension.dataType.name)
            .bind("isRequired", extension.isRequired)
            .bind("createdAt", extension.createdAt ?: LocalDateTime.now())
            .bindNullable("createdBy", extension.createdBy)
            .map { row -> rowToExtension(row) }
            .awaitOne()
    }

    private suspend fun update(extension: Extension): Extension {
        val sql = """
            UPDATE extensions SET
                name = :name,
                description = :description,
                data_type = :dataType,
                is_required = :isRequired,
                updated_at = :updatedAt,
                updated_by = :updatedBy
            WHERE id = :id
            RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("id", extension.id!!)
            .bind("name", extension.name)
            .bindNullable("description", extension.description)
            .bind("dataType", extension.dataType.name)
            .bind("isRequired", extension.isRequired)
            .bind("updatedAt", LocalDateTime.now())
            .bindNullable("updatedBy", extension.updatedBy)
            .map { row -> rowToExtension(row) }
            .awaitOne()
    }

    override suspend fun findById(id: Long): Extension? {
        val sql = "SELECT * FROM extensions WHERE id = :id"

        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToExtension(row) }
            .awaitOneOrNull()
    }

    override suspend fun findByCode(code: String): Extension? {
        val sql = "SELECT * FROM extensions WHERE code = :code"

        return databaseClient.sql(sql)
            .bind("code", code)
            .map { row -> rowToExtension(row) }
            .awaitOneOrNull()
    }

    override fun findAll(): Flow<Extension> {
        val sql = "SELECT * FROM extensions ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .map { row -> rowToExtension(row) }
            .all()
            .asFlow()
    }

    override fun findAllRequired(): Flow<Extension> {
        val sql = "SELECT * FROM extensions WHERE is_required = true ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .map { row -> rowToExtension(row) }
            .all()
            .asFlow()
    }

    override suspend fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM extensions WHERE id = :id"

        val rows = databaseClient.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return rows > 0
    }

    override suspend fun existsByCode(code: String): Boolean {
        val sql = "SELECT COUNT(*) FROM extensions WHERE code = :code"

        val count = databaseClient.sql(sql)
            .bind("code", code)
            .map { row -> row.get(0, java.lang.Long::class.java) }
            .awaitOneOrNull() ?: 0L

        return count > 0
    }

    private fun rowToExtension(row: Row): Extension {
        return Extension(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            code = row.get("code", String::class.java)!!,
            name = row.get("name", String::class.java)!!,
            description = row.get("description", String::class.java),
            dataType = ExtensionDataType.fromName(row.get("data_type", String::class.java)!!),
            isRequired = row.get("is_required", java.lang.Boolean::class.java)!!,
            createdAt = row.get("created_at", LocalDateTime::class.java),
            createdBy = row.get("created_by", String::class.java),
            updatedAt = row.get("updated_at", LocalDateTime::class.java),
            updatedBy = row.get("updated_by", String::class.java)
        )
    }

    private fun DatabaseClient.GenericExecuteSpec.bindNullable(name: String, value: Any?): DatabaseClient.GenericExecuteSpec {
        return if (value != null) {
            this.bind(name, value)
        } else {
            this.bindNull(name, Any::class.java)
        }
    }
}

/**
 * RequestExtension Repository 구현체 (R2DBC)
 */
@Repository
class R2dbcRequestExtensionRepository(
    private val databaseClient: DatabaseClient
) : RequestExtensionRepository {

    override suspend fun save(requestExtension: RequestExtension): RequestExtension {
        val sql = """
            INSERT INTO request_extensions (
                request_id, extension_code, value,
                created_at, created_by
            ) VALUES (
                :requestId, :extensionCode, :value,
                :createdAt, :createdBy
            )
            ON CONFLICT (request_id, extension_code)
            DO UPDATE SET
                value = EXCLUDED.value
            RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("requestId", requestExtension.requestId)
            .bind("extensionCode", requestExtension.extensionCode)
            .bind("value", requestExtension.value)
            .bind("createdAt", requestExtension.createdAt ?: LocalDateTime.now())
            .bindNullable("createdBy", requestExtension.createdBy)
            .map { row -> rowToRequestExtension(row) }
            .awaitOne()
    }

    override suspend fun findById(id: Long): RequestExtension? {
        val sql = "SELECT * FROM request_extensions WHERE id = :id"

        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToRequestExtension(row) }
            .awaitOneOrNull()
    }

    override fun findByRequestId(requestId: Long): Flow<RequestExtension> {
        val sql = "SELECT * FROM request_extensions WHERE request_id = :requestId ORDER BY created_at"

        return databaseClient.sql(sql)
            .bind("requestId", requestId)
            .map { row -> rowToRequestExtension(row) }
            .all()
            .asFlow()
    }

    override suspend fun findByRequestIdAndCode(requestId: Long, extensionCode: String): RequestExtension? {
        val sql = """
            SELECT * FROM request_extensions
            WHERE request_id = :requestId
            AND extension_code = :code
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("requestId", requestId)
            .bind("code", extensionCode)
            .map { row -> rowToRequestExtension(row) }
            .awaitOneOrNull()
    }

    override suspend fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM request_extensions WHERE id = :id"

        val rows = databaseClient.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return rows > 0
    }

    override suspend fun deleteByRequestId(requestId: Long): Int {
        val sql = "DELETE FROM request_extensions WHERE request_id = :requestId"

        return databaseClient.sql(sql)
            .bind("requestId", requestId)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
            .toInt()
    }

    private fun rowToRequestExtension(row: Row): RequestExtension {
        return RequestExtension(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            requestId = row.get("request_id", java.lang.Long::class.java)!!.toLong(),
            extensionCode = row.get("extension_code", String::class.java)!!,
            value = row.get("value", String::class.java)!!,
            createdAt = row.get("created_at", LocalDateTime::class.java),
            createdBy = row.get("created_by", String::class.java)
        )
    }

    private fun DatabaseClient.GenericExecuteSpec.bindNullable(name: String, value: Any?): DatabaseClient.GenericExecuteSpec {
        return if (value != null) {
            this.bind(name, value)
        } else {
            this.bindNull(name, Any::class.java)
        }
    }
}
