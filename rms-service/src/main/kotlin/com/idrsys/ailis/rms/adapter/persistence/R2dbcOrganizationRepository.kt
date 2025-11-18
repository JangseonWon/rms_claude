package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Organization
import com.idrsys.ailis.rms.domain.repository.OrganizationRepository
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Organization Repository 구현체 (R2DBC)
 */
@Repository
class R2dbcOrganizationRepository(
    private val databaseClient: DatabaseClient
) : OrganizationRepository {

    override suspend fun save(organization: Organization): Organization {
        return if (organization.id == null) {
            insert(organization)
        } else {
            update(organization)
        }
    }

    private suspend fun insert(organization: Organization): Organization {
        val sql = """
            INSERT INTO organizations (
                serial, name, registration_number, nursing_number,
                branch_code, branch_name, employee_id, employee_name,
                employee_phone, type, created_at, created_by
            ) VALUES (
                :serial, :name, :registrationNumber, :nursingNumber,
                :branchCode, :branchName, :employeeId, :employeeName,
                :employeePhone, :type, :createdAt, :createdBy
            ) RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("serial", organization.serial)
            .bind("name", organization.name)
            .bindNullable("registrationNumber", organization.registrationNumber)
            .bindNullable("nursingNumber", organization.nursingNumber)
            .bindNullable("branchCode", organization.branchCode)
            .bindNullable("branchName", organization.branchName)
            .bindNullable("employeeId", organization.employeeId)
            .bindNullable("employeeName", organization.employeeName)
            .bindNullable("employeePhone", organization.employeePhone)
            .bindNullable("type", organization.type)
            .bind("createdAt", organization.createdAt ?: LocalDateTime.now())
            .bindNullable("createdBy", organization.createdBy)
            .map { row -> rowToOrganization(row) }
            .awaitOne()
    }

    private suspend fun update(organization: Organization): Organization {
        val sql = """
            UPDATE organizations SET
                name = :name,
                registration_number = :registrationNumber,
                nursing_number = :nursingNumber,
                branch_code = :branchCode,
                branch_name = :branchName,
                employee_id = :employeeId,
                employee_name = :employeeName,
                employee_phone = :employeePhone,
                type = :type,
                updated_at = :updatedAt,
                updated_by = :updatedBy
            WHERE id = :id
            RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("id", organization.id!!)
            .bind("name", organization.name)
            .bindNullable("registrationNumber", organization.registrationNumber)
            .bindNullable("nursingNumber", organization.nursingNumber)
            .bindNullable("branchCode", organization.branchCode)
            .bindNullable("branchName", organization.branchName)
            .bindNullable("employeeId", organization.employeeId)
            .bindNullable("employeeName", organization.employeeName)
            .bindNullable("employeePhone", organization.employeePhone)
            .bindNullable("type", organization.type)
            .bind("updatedAt", LocalDateTime.now())
            .bindNullable("updatedBy", organization.updatedBy)
            .map { row -> rowToOrganization(row) }
            .awaitOne()
    }

    override suspend fun findById(id: Long): Organization? {
        val sql = "SELECT * FROM organizations WHERE id = :id"

        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToOrganization(row) }
            .awaitOneOrNull()
    }

    override suspend fun findBySerial(serial: String): Organization? {
        val sql = "SELECT * FROM organizations WHERE serial = :serial"

        return databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> rowToOrganization(row) }
            .awaitOneOrNull()
    }

    override fun findByName(name: String): Flow<Organization> {
        val sql = "SELECT * FROM organizations WHERE name LIKE :name ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .bind("name", "%$name%")
            .map { row -> rowToOrganization(row) }
            .all()
            .asFlow()
    }

    override fun findAll(): Flow<Organization> {
        val sql = "SELECT * FROM organizations ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .map { row -> rowToOrganization(row) }
            .all()
            .asFlow()
    }

    override suspend fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM organizations WHERE id = :id"

        val rows = databaseClient.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return rows > 0
    }

    override suspend fun existsBySerial(serial: String): Boolean {
        val sql = "SELECT COUNT(*) FROM organizations WHERE serial = :serial"

        val count = databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> row.get(0, java.lang.Long::class.java) }
            .awaitOneOrNull() ?: 0L

        return count > 0
    }

    override suspend fun hasRequests(organizationId: Long): Boolean {
        val sql = "SELECT COUNT(*) FROM requests WHERE organization_id = :orgId"

        val count = databaseClient.sql(sql)
            .bind("orgId", organizationId)
            .map { row -> row.get(0, java.lang.Long::class.java) }
            .awaitOneOrNull() ?: 0L

        return count > 0
    }

    private fun rowToOrganization(row: Row): Organization {
        return Organization(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            serial = row.get("serial", String::class.java)!!,
            name = row.get("name", String::class.java)!!,
            registrationNumber = row.get("registration_number", String::class.java),
            nursingNumber = row.get("nursing_number", String::class.java),
            branchCode = row.get("branch_code", String::class.java),
            branchName = row.get("branch_name", String::class.java),
            employeeId = row.get("employee_id", String::class.java),
            employeeName = row.get("employee_name", String::class.java),
            employeePhone = row.get("employee_phone", String::class.java),
            type = row.get("type", String::class.java),
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
