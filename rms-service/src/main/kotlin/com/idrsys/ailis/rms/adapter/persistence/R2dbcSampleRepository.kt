package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Sample
import com.idrsys.ailis.rms.domain.model.SampleType
import com.idrsys.ailis.rms.domain.repository.SampleRepository
import com.idrsys.ailis.rms.domain.repository.SampleTypeRepository
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
 * SampleType Repository 구현체 (R2DBC)
 */
@Repository
class R2dbcSampleTypeRepository(
    private val databaseClient: DatabaseClient
) : SampleTypeRepository {

    override suspend fun save(sampleType: SampleType): SampleType {
        return if (sampleType.id == null) {
            insert(sampleType)
        } else {
            update(sampleType)
        }
    }

    private suspend fun insert(sampleType: SampleType): SampleType {
        val sql = """
            INSERT INTO sample_types (
                serial, name, description,
                created_at, created_by
            ) VALUES (
                :serial, :name, :description,
                :createdAt, :createdBy
            ) RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("serial", sampleType.serial)
            .bind("name", sampleType.name)
            .bindNullable("description", sampleType.description)
            .bind("createdAt", sampleType.createdAt ?: LocalDateTime.now())
            .bindNullable("createdBy", sampleType.createdBy)
            .map { row -> rowToSampleType(row) }
            .awaitOne()
    }

    private suspend fun update(sampleType: SampleType): SampleType {
        val sql = """
            UPDATE sample_types SET
                name = :name,
                description = :description,
                updated_at = :updatedAt,
                updated_by = :updatedBy
            WHERE id = :id
            RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("id", sampleType.id!!)
            .bind("name", sampleType.name)
            .bindNullable("description", sampleType.description)
            .bind("updatedAt", LocalDateTime.now())
            .bindNullable("updatedBy", sampleType.updatedBy)
            .map { row -> rowToSampleType(row) }
            .awaitOne()
    }

    override suspend fun findById(id: Long): SampleType? {
        val sql = "SELECT * FROM sample_types WHERE id = :id"

        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToSampleType(row) }
            .awaitOneOrNull()
    }

    override suspend fun findBySerial(serial: String): SampleType? {
        val sql = "SELECT * FROM sample_types WHERE serial = :serial"

        return databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> rowToSampleType(row) }
            .awaitOneOrNull()
    }

    override fun findAll(): Flow<SampleType> {
        val sql = "SELECT * FROM sample_types ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .map { row -> rowToSampleType(row) }
            .all()
            .asFlow()
    }

    override suspend fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM sample_types WHERE id = :id"

        val rows = databaseClient.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return rows > 0
    }

    override suspend fun existsBySerial(serial: String): Boolean {
        val sql = "SELECT COUNT(*) FROM sample_types WHERE serial = :serial"

        val count = databaseClient.sql(sql)
            .bind("serial", serial)
            .map { row -> row.get(0, java.lang.Long::class.java) }
            .awaitOneOrNull() ?: 0L

        return count > 0
    }

    private fun rowToSampleType(row: Row): SampleType {
        return SampleType(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            serial = row.get("serial", String::class.java)!!,
            name = row.get("name", String::class.java)!!,
            description = row.get("description", String::class.java),
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
 * Sample Repository 구현체 (R2DBC)
 */
@Repository
class R2dbcSampleRepository(
    private val databaseClient: DatabaseClient
) : SampleRepository {

    override suspend fun save(sample: Sample): Sample {
        return if (sample.id == null) {
            insert(sample)
        } else {
            update(sample)
        }
    }

    private suspend fun insert(sample: Sample): Sample {
        val sql = """
            INSERT INTO samples (
                count, age, sampling_on, sample_type_id,
                created_at, created_by
            ) VALUES (
                :count, :age, :samplingOn, :sampleTypeId,
                :createdAt, :createdBy
            ) RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("count", sample.count)
            .bindNullable("age", sample.age)
            .bind("samplingOn", sample.samplingOn)
            .bind("sampleTypeId", sample.sampleTypeId)
            .bind("createdAt", sample.createdAt ?: LocalDateTime.now())
            .bindNullable("createdBy", sample.createdBy)
            .map { row -> rowToSample(row) }
            .awaitOne()
    }

    private suspend fun update(sample: Sample): Sample {
        val sql = """
            UPDATE samples SET
                count = :count,
                age = :age,
                sampling_on = :samplingOn,
                sample_type_id = :sampleTypeId,
                updated_at = :updatedAt,
                updated_by = :updatedBy
            WHERE id = :id
            RETURNING *
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("id", sample.id!!)
            .bind("count", sample.count)
            .bindNullable("age", sample.age)
            .bind("samplingOn", sample.samplingOn)
            .bind("sampleTypeId", sample.sampleTypeId)
            .bind("updatedAt", LocalDateTime.now())
            .bindNullable("updatedBy", sample.updatedBy)
            .map { row -> rowToSample(row) }
            .awaitOne()
    }

    override suspend fun findById(id: Long): Sample? {
        val sql = "SELECT * FROM samples WHERE id = :id"

        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToSample(row) }
            .awaitOneOrNull()
    }

    override fun findBySampleTypeId(sampleTypeId: Long): Flow<Sample> {
        val sql = "SELECT * FROM samples WHERE sample_type_id = :sampleTypeId ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .bind("sampleTypeId", sampleTypeId)
            .map { row -> rowToSample(row) }
            .all()
            .asFlow()
    }

    override fun findAll(): Flow<Sample> {
        val sql = "SELECT * FROM samples ORDER BY created_at DESC"

        return databaseClient.sql(sql)
            .map { row -> rowToSample(row) }
            .all()
            .asFlow()
    }

    override suspend fun deleteById(id: Long): Boolean {
        val sql = "DELETE FROM samples WHERE id = :id"

        val rows = databaseClient.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()

        return rows > 0
    }

    private fun rowToSample(row: Row): Sample {
        return Sample(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            count = row.get("count", Integer::class.java)!!.toInt(),
            age = row.get("age", Integer::class.java)?.toInt(),
            samplingOn = row.get("sampling_on", LocalDate::class.java)!!,
            sampleTypeId = row.get("sample_type_id", java.lang.Long::class.java)!!.toLong(),
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
