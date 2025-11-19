package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Sample
import com.idrsys.ailis.rms.domain.model.SampleType
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * R2dbcSampleRepository 통합 테스트
 *
 * 실제 PostgreSQL 연결 필요:
 * - docker-compose up -d postgres
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class R2dbcSampleRepositoryTest {

    @Autowired
    private lateinit var sampleRepository: R2dbcSampleRepository

    @Autowired
    private lateinit var sampleTypeRepository: R2dbcSampleTypeRepository

    @Test
    fun `검체 타입 생성 테스트`() = runBlocking {
        // Given
        val sampleType = SampleType.create(
            serial = "BLD",
            name = "혈액",
            createdBy = "test"
        )

        // When
        val saved = sampleTypeRepository.save(sampleType)

        // Then
        assertNotNull(saved.id)
        assertEquals("BLD", saved.serial)
        assertEquals("혈액", saved.name)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `검체 타입 조회 테스트 - 일련번호로 조회`() = runBlocking {
        // Given
        val sampleType = SampleType.create(serial = "URN", name = "소변", createdBy = "test")
        sampleTypeRepository.save(sampleType)

        // When
        val found = sampleTypeRepository.findBySerial("URN")

        // Then
        assertNotNull(found)
        assertEquals("URN", found!!.serial)
        assertEquals("소변", found.name)
    }

    @Test
    fun `검체 타입 목록 조회 테스트`() = runBlocking {
        // Given
        sampleTypeRepository.save(SampleType.create(serial = "ST1", name = "타입1", createdBy = "test"))
        sampleTypeRepository.save(SampleType.create(serial = "ST2", name = "타입2", createdBy = "test"))

        // When
        val types = sampleTypeRepository.findAll().toList()

        // Then
        assertTrue(types.size >= 2)
        assertTrue(types.any { it.serial == "ST1" })
        assertTrue(types.any { it.serial == "ST2" })
    }

    @Test
    fun `검체 생성 테스트`() = runBlocking {
        // Given - 먼저 검체 타입 생성
        val sampleType = SampleType.create(serial = "BLD001", name = "전혈", createdBy = "test")
        val savedType = sampleTypeRepository.save(sampleType)

        // When - 검체 생성
        val sample = Sample.create(
            count = 2,
            samplingOn = LocalDate.of(2024, 1, 15),
            sampleTypeId = savedType.id!!,
            age = 5,
            createdBy = "test"
        )
        val saved = sampleRepository.save(sample)

        // Then
        assertNotNull(saved.id)
        assertEquals(2, saved.count)
        assertEquals(LocalDate.of(2024, 1, 15), saved.samplingOn)
        assertEquals(savedType.id, saved.sampleTypeId)
        assertEquals(5, saved.age)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `검체 조회 테스트 - ID로 조회`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(serial = "BLD002", name = "혈청", createdBy = "test"))
        val sample = Sample.create(
            count = 1,
            samplingOn = LocalDate.now(),
            sampleTypeId = sampleType.id!!,
            createdBy = "test"
        )
        val saved = sampleRepository.save(sample)

        // When
        val found = sampleRepository.findById(saved.id!!)

        // Then
        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertEquals(1, found.count)
        assertEquals(sampleType.id, found.sampleTypeId)
    }

    @Test
    fun `검체 타입별 조회 테스트`() = runBlocking {
        // Given
        val type1 = sampleTypeRepository.save(SampleType.create(serial = "TYPE1", name = "타입1", createdBy = "test"))
        val type2 = sampleTypeRepository.save(SampleType.create(serial = "TYPE2", name = "타입2", createdBy = "test"))

        sampleRepository.save(Sample.create(count = 1, samplingOn = LocalDate.now(), sampleTypeId = type1.id!!, createdBy = "test"))
        sampleRepository.save(Sample.create(count = 2, samplingOn = LocalDate.now(), sampleTypeId = type1.id!!, createdBy = "test"))
        sampleRepository.save(Sample.create(count = 1, samplingOn = LocalDate.now(), sampleTypeId = type2.id!!, createdBy = "test"))

        // When
        val type1Samples = sampleRepository.findBySampleTypeId(type1.id!!).toList()

        // Then
        assertTrue(type1Samples.size >= 2)
        assertTrue(type1Samples.all { it.sampleTypeId == type1.id })
    }

    @Test
    fun `검체 수정 테스트`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(serial = "BLD004", name = "타입", createdBy = "test"))
        val sample = Sample.create(
            count = 1,
            samplingOn = LocalDate.of(2024, 1, 1),
            sampleTypeId = sampleType.id!!,
            age = 3,
            createdBy = "test"
        )
        val saved = sampleRepository.save(sample)

        // When
        val updated = saved.update(
            count = 3,
            age = 7,
            updatedBy = "updater"
        )
        val result = sampleRepository.save(updated)

        // Then
        assertEquals(saved.id, result.id)
        assertEquals(3, result.count)
        assertEquals(7, result.age)
        assertEquals("updater", result.updatedBy)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `검체 삭제 테스트`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(serial = "BLD005", name = "타입", createdBy = "test"))
        val sample = Sample.create(
            count = 1,
            samplingOn = LocalDate.now(),
            sampleTypeId = sampleType.id!!,
            createdBy = "test"
        )
        val saved = sampleRepository.save(sample)

        // When
        val deleted = sampleRepository.deleteById(saved.id!!)

        // Then
        assertTrue(deleted)

        // Verify
        val found = sampleRepository.findById(saved.id!!)
        assertNull(found)
    }

    @Test
    fun `검체 목록 조회 테스트`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(serial = "BLD006", name = "타입", createdBy = "test"))
        sampleRepository.save(Sample.create(count = 1, samplingOn = LocalDate.now(), sampleTypeId = sampleType.id!!, createdBy = "test"))
        sampleRepository.save(Sample.create(count = 2, samplingOn = LocalDate.now(), sampleTypeId = sampleType.id!!, createdBy = "test"))

        // When
        val samples = sampleRepository.findAll().toList()

        // Then
        assertTrue(samples.size >= 2)
    }

    @Test
    fun `중복 검체 타입 일련번호 체크 테스트`() = runBlocking {
        // Given
        val serial = "DUPSERIAL"
        sampleTypeRepository.save(SampleType.create(serial = serial, name = "중복테스트", createdBy = "test"))

        // When
        val exists = sampleTypeRepository.existsBySerial(serial)

        // Then
        assertTrue(exists)

        // 존재하지 않는 경우
        val notExists = sampleTypeRepository.existsBySerial("NOTEXIST")
        assertFalse(notExists)
    }

    @Test
    fun `검체 나이 업데이트 테스트`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(serial = "BLD007", name = "타입", createdBy = "test"))
        val sample = Sample.create(
            count = 1,
            samplingOn = LocalDate.now(),
            sampleTypeId = sampleType.id!!,
            age = 5,
            createdBy = "test"
        )
        val saved = sampleRepository.save(sample)

        // When
        val updated = saved.update(age = 10, updatedBy = "updater")
        val result = sampleRepository.save(updated)

        // Then
        assertEquals(10, result.age)
    }

    @Test
    fun `검체 샘플 타입 변경 테스트`() = runBlocking {
        // Given
        val type1 = sampleTypeRepository.save(SampleType.create(serial = "TYPE_A", name = "타입A", createdBy = "test"))
        val type2 = sampleTypeRepository.save(SampleType.create(serial = "TYPE_B", name = "타입B", createdBy = "test"))

        val sample = Sample.create(
            count = 1,
            samplingOn = LocalDate.now(),
            sampleTypeId = type1.id!!,
            createdBy = "test"
        )
        val saved = sampleRepository.save(sample)

        // When - 샘플 타입 변경
        val updated = saved.update(sampleTypeId = type2.id!!, updatedBy = "updater")
        val result = sampleRepository.save(updated)

        // Then
        assertEquals(type2.id, result.sampleTypeId)
    }
}
