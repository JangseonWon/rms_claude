package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Service
import com.idrsys.ailis.rms.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitOne
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Service Repository R2DBC 구현체
 *
 * R2DBC를 사용한 서비스 데이터 접근 구현
 */
@Repository
class R2dbcServiceRepository(
    private val databaseClient: DatabaseClient
) : ServiceRepository {

    override suspend fun save(service: Service): Service {
        return if (service.id == null) {
            insert(service)
        } else {
            update(service)
        }
    }

    private suspend fun insert(service: Service): Service {
        val sql = """
            INSERT INTO services (serial, name, description, price, category_id, created_at, created_by)
            VALUES (:serial, :name, :description, :price, :categoryId, :createdAt, :createdBy)
            RETURNING *
        """

        return databaseClient.sql(sql)
            .bind("serial", service.serial)
            .bind("name", service.name)
            .bind("description", service.description ?: "")
            .bind("price", service.price ?: 0)
            .bind("categoryId", service.categoryId ?: 0)
            .bind("createdAt", service.createdAt ?: LocalDateTime.now())
            .bind("createdBy", service.createdBy ?: "")
            .map { row -> rowToService(row) }
            .awaitOne()
    }

    private suspend fun update(service: Service): Service {
        val sql = """
            UPDATE services
            SET name = :name,
                description = :description,
                price = :price,
                category_id = :categoryId,
                updated_at = :updatedAt,
                updated_by = :updatedBy
            WHERE id = :id
            RETURNING *
        """

        return databaseClient.sql(sql)
            .bind("id", service.id!!)
            .bind("name", service.name)
            .bind("description", service.description ?: "")
            .bind("price", service.price ?: 0)
            .bind("categoryId", service.categoryId ?: 0)
            .bind("updatedAt", service.updatedAt ?: LocalDateTime.now())
            .bind("updatedBy", service.updatedBy ?: "")
            .map { row -> rowToService(row) }
            .awaitOne()
    }

    override suspend fun findById(id: Long): Service? {
        val sql = "SELECT * FROM services WHERE id = :id"

        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToService(row) }
            .awaitFirstOrNull()
    }

    override suspend fun findBySerial(serial: String): Service? {
        val sql = "SELECT * FROM services WHERE serial = :serial"

        return databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> rowToService(row) }
            .awaitFirstOrNull()
    }

    override fun findByCategoryId(categoryId: Long): Flow<Service> {
        val sql = "SELECT * FROM services WHERE category_id = :categoryId ORDER BY id"

        return databaseClient.sql(sql)
            .bind("categoryId", categoryId)
            .map { row -> rowToService(row) }
            .all()
            .asFlow()
    }

    override fun findAll(): Flow<Service> {
        val sql = "SELECT * FROM services ORDER BY id"

        return databaseClient.sql(sql)
            .map { row -> rowToService(row) }
            .all()
            .asFlow()
    }

    override suspend fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM services WHERE id = :id"

        val rowsUpdated = databaseClient.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitOne()

        return rowsUpdated > 0
    }

    override suspend fun existsBySerial(serial: String): Boolean {
        val sql = "SELECT COUNT(*) as cnt FROM services WHERE serial = :serial"

        val count = databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> row.get("cnt", java.lang.Long::class.java)?.toLong() ?: 0L }
            .awaitOne()

        return count > 0
    }

    /**
     * Row를 Service 도메인 모델로 변환
     */
    private fun rowToService(row: io.r2dbc.spi.Row): Service {
        return Service(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            serial = row.get("serial", String::class.java) ?: "",
            name = row.get("name", String::class.java) ?: "",
            description = row.get("description", String::class.java),
            price = row.get("price", java.lang.Long::class.java)?.toLong(),
            categoryId = row.get("category_id", java.lang.Long::class.java)?.toLong(),
            createdAt = row.get("created_at", LocalDateTime::class.java),
            createdBy = row.get("created_by", String::class.java),
            updatedAt = row.get("updated_at", LocalDateTime::class.java),
            updatedBy = row.get("updated_by", String::class.java)
        )
    }
}
