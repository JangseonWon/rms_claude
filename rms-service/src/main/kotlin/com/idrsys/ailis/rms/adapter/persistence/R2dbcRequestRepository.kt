package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Request
import com.idrsys.ailis.rms.domain.repository.RequestRepository
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Request Repository 구현체 (R2DBC)
 */
@Repository
class R2dbcRequestRepository(
    private val databaseClient: DatabaseClient
) : RequestRepository {

    override suspend fun save(request: Request): Request {
        return if (request.id == null) {
            insert(request)
        } else {
            update(request)
        }
    }

    private suspend fun insert(request: Request): Request {
        val sql = """
            INSERT INTO requests (
                serial, request_date_from, request_date_to,
                department, ward, physician, memo,
                genome_price, labs_price,
                organization_id, patient_id, sample_id,
                created_at, created_by
            ) VALUES (
                :serial, :requestDateFrom, :requestDateTo,
                :department, :ward, :physician, :memo,
                :genomePrice, :labsPrice,
                :organizationId, :patientId, :sampleId,
                :createdAt, :createdBy
            ) RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("serial", request.serial)
            .bind("requestDateFrom", request.requestDateFrom)
            .bind("requestDateTo", request.requestDateTo)
            .bindNullable("department", request.department)
            .bindNullable("ward", request.ward)
            .bindNullable("physician", request.physician)
            .bindNullable("memo", request.memo)
            .bindNullable("genomePrice", request.genomePrice)
            .bindNullable("labsPrice", request.labsPrice)
            .bind("organizationId", request.organizationId)
            .bind("patientId", request.patientId)
            .bind("sampleId", request.sampleId)
            .bind("createdAt", request.createdAt ?: LocalDateTime.now())
            .bindNullable("createdBy", request.createdBy)
            .map { row -> rowToRequest(row) }
            .awaitOne()
    }

    private suspend fun update(request: Request): Request {
        val sql = """
            UPDATE requests SET
                department = :department,
                ward = :ward,
                physician = :physician,
                memo = :memo,
                genome_price = :genomePrice,
                labs_price = :labsPrice,
                updated_at = :updatedAt,
                updated_by = :updatedBy
            WHERE id = :id
            RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("id", request.id!!)
            .bindNullable("department", request.department)
            .bindNullable("ward", request.ward)
            .bindNullable("physician", request.physician)
            .bindNullable("memo", request.memo)
            .bindNullable("genomePrice", request.genomePrice)
            .bindNullable("labsPrice", request.labsPrice)
            .bind("updatedAt", LocalDateTime.now())
            .bindNullable("updatedBy", request.updatedBy)
            .map { row -> rowToRequest(row) }
            .awaitOne()
    }

    override suspend fun findById(id: Long): Request? {
        val sql = "SELECT * FROM requests WHERE id = :id"

        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToRequest(row) }
            .awaitOneOrNull()
    }

    override suspend fun findBySerial(serial: String): Request? {
        val sql = "SELECT * FROM requests WHERE serial = :serial"

        return databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> rowToRequest(row) }
            .awaitOneOrNull()
    }

    override fun findAll(): Flow<Request> {
        val sql = "SELECT * FROM requests ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .map { row -> rowToRequest(row) }
            .all()
            .asFlow()
    }

    override fun findByOrganizationId(organizationId: Long): Flow<Request> {
        val sql = "SELECT * FROM requests WHERE organization_id = :orgId ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .bind("orgId", organizationId)
            .map { row -> rowToRequest(row) }
            .all()
            .asFlow()
    }

    override fun findByPatientId(patientId: Long): Flow<Request> {
        val sql = "SELECT * FROM requests WHERE patient_id = :patientId ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .bind("patientId", patientId)
            .map { row -> rowToRequest(row) }
            .all()
            .asFlow()
    }

    override fun findByDateRange(dateFrom: LocalDate, dateTo: LocalDate): Flow<Request> {
        val sql = """
            SELECT * FROM requests
            WHERE request_date_from >= :dateFrom
            AND request_date_to <= :dateTo
            ORDER BY created_at DESC
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("dateFrom", dateFrom)
            .bind("dateTo", dateTo)
            .map { row -> rowToRequest(row) }
            .all()
            .asFlow()
    }

    override suspend fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM requests WHERE id = :id"

        val rows = databaseClient.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return rows > 0
    }

    override suspend fun existsBySerial(serial: String): Boolean {
        val sql = "SELECT COUNT(*) FROM requests WHERE serial = :serial"

        val count = databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> row.get(0, java.lang.Long::class.java) }
            .awaitOneOrNull() ?: 0L

        return count > 0
    }

    private fun rowToRequest(row: Row): Request {
        return Request(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            serial = row.get("serial", String::class.java)!!,
            requestDateFrom = row.get("request_date_from", LocalDate::class.java)!!,
            requestDateTo = row.get("request_date_to", LocalDate::class.java)!!,
            department = row.get("department", String::class.java),
            ward = row.get("ward", String::class.java),
            physician = row.get("physician", String::class.java),
            memo = row.get("memo", String::class.java),
            genomePrice = row.get("genome_price", java.lang.Long::class.java)?.toLong(),
            labsPrice = row.get("labs_price", java.lang.Long::class.java)?.toLong(),
            organizationId = row.get("organization_id", java.lang.Long::class.java)!!.toLong(),
            patientId = row.get("patient_id", java.lang.Long::class.java)!!.toLong(),
            sampleId = row.get("sample_id", java.lang.Long::class.java)!!.toLong(),
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
