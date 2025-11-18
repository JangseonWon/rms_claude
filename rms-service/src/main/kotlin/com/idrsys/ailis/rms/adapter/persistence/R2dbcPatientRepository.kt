package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Patient
import com.idrsys.ailis.rms.domain.model.Sex
import com.idrsys.ailis.rms.domain.repository.PatientRepository
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
 * Patient Repository 구현체 (R2DBC)
 */
@Repository
class R2dbcPatientRepository(
    private val databaseClient: DatabaseClient
) : PatientRepository {

    override suspend fun save(patient: Patient): Patient {
        return if (patient.id == null) {
            insert(patient)
        } else {
            update(patient)
        }
    }

    private suspend fun insert(patient: Patient): Patient {
        val sql = """
            INSERT INTO patients (
                serial, name, sex, age, birth,
                created_at, created_by
            ) VALUES (
                :serial, :name, :sex, :age, :birth,
                :createdAt, :createdBy
            ) RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("serial", patient.serial)
            .bind("name", patient.name)
            .bindNullable("sex", patient.sex?.code)
            .bindNullable("age", patient.age)
            .bindNullable("birth", patient.birth)
            .bind("createdAt", patient.createdAt ?: LocalDateTime.now())
            .bindNullable("createdBy", patient.createdBy)
            .map { row -> rowToPatient(row) }
            .awaitOne()
    }

    private suspend fun update(patient: Patient): Patient {
        val sql = """
            UPDATE patients SET
                name = :name,
                sex = :sex,
                age = :age,
                birth = :birth,
                updated_at = :updatedAt,
                updated_by = :updatedBy
            WHERE id = :id
            RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("id", patient.id!!)
            .bind("name", patient.name)
            .bindNullable("sex", patient.sex?.code)
            .bindNullable("age", patient.age)
            .bindNullable("birth", patient.birth)
            .bind("updatedAt", LocalDateTime.now())
            .bindNullable("updatedBy", patient.updatedBy)
            .map { row -> rowToPatient(row) }
            .awaitOne()
    }

    override suspend fun findById(id: Long): Patient? {
        val sql = "SELECT * FROM patients WHERE id = :id"

        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToPatient(row) }
            .awaitOneOrNull()
    }

    override suspend fun findBySerial(serial: String): Patient? {
        val sql = "SELECT * FROM patients WHERE serial = :serial"

        return databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> rowToPatient(row) }
            .awaitOneOrNull()
    }

    override fun findByName(name: String): Flow<Patient> {
        val sql = "SELECT * FROM patients WHERE name LIKE :name ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .bind("name", "%$name%")
            .map { row -> rowToPatient(row) }
            .all()
            .asFlow()
    }

    override fun findAll(): Flow<Patient> {
        val sql = "SELECT * FROM patients ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .map { row -> rowToPatient(row) }
            .all()
            .asFlow()
    }

    override suspend fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM patients WHERE id = :id"

        val rows = databaseClient.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return rows > 0
    }

    override suspend fun existsBySerial(serial: String): Boolean {
        val sql = "SELECT COUNT(*) FROM patients WHERE serial = :serial"

        val count = databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> row.get(0, java.lang.Long::class.java) }
            .awaitOneOrNull() ?: 0L

        return count > 0
    }

    private fun rowToPatient(row: Row): Patient {
        return Patient(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            serial = row.get("serial", String::class.java)!!,
            name = row.get("name", String::class.java)!!,
            sex = row.get("sex", String::class.java)?.let { Sex.fromCode(it) },
            age = row.get("age", Integer::class.java)?.toInt(),
            birth = row.get("birth", LocalDate::class.java),
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
