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
            code = "BLD",
            name = "혈액",
            createdBy = "test"
        )

        // When
        val saved = sampleTypeRepository.save(sampleType)

        // Then
        assertNotNull(saved.id)
        assertEquals("BLD", saved.code)
        assertEquals("혈액", saved.name)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `검체 타입 조회 테스트 - 코드로 조회`() = runBlocking {
        // Given
        val sampleType = SampleType.create(code = "URN", name = "소변", createdBy = "test")
        sampleTypeRepository.save(sampleType)

        // When
        val found = sampleTypeRepository.findByCode("URN")

        // Then
        assertNotNull(found)
        assertEquals("URN", found!!.code)
        assertEquals("소변", found.name)
    }

    @Test
    fun `검체 타입 목록 조회 테스트`() = runBlocking {
        // Given
        sampleTypeRepository.save(SampleType.create(code = "ST1", name = "타입1", createdBy = "test"))
        sampleTypeRepository.save(SampleType.create(code = "ST2", name = "타입2", createdBy = "test"))

        // When
        val types = sampleTypeRepository.findAll().toList()

        // Then
        assertTrue(types.size >= 2)
        assertTrue(types.any { it.code == "ST1" })
        assertTrue(types.any { it.code == "ST2" })
    }

    @Test
    fun `검체 생성 테스트`() = runBlocking {
        // Given - 먼저 검체 타입 생성
        val sampleType = SampleType.create(code = "BLD001", name = "전혈", createdBy = "test")
        val savedType = sampleTypeRepository.save(sampleType)

        // When - 검체 생성
        val sample = Sample.create(
            serial = "S001",
            sampleTypeId = savedType.id!!,
            createdBy = "test"
        )
        val saved = sampleRepository.save(sample)

        // Then
        assertNotNull(saved.id)
        assertEquals("S001", saved.serial)
        assertEquals(savedType.id, saved.sampleTypeId)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `검체 조회 테스트 - ID로 조회`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(code = "BLD002", name = "혈청", createdBy = "test"))
        val sample = Sample.create(serial = "S002", sampleTypeId = sampleType.id!!, createdBy = "test")
        val saved = sampleRepository.save(sample)

        // When
        val found = sampleRepository.findById(saved.id!!)

        // Then
        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertEquals("S002", found.serial)
        assertEquals(sampleType.id, found.sampleTypeId)
    }

    @Test
    fun `검체 조회 테스트 - 일련번호로 조회`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(code = "BLD003", name = "혈장", createdBy = "test"))
        val sample = Sample.create(serial = "S003", sampleTypeId = sampleType.id!!, createdBy = "test")
        sampleRepository.save(sample)

        // When
        val found = sampleRepository.findBySerial("S003")

        // Then
        assertNotNull(found)
        assertEquals("S003", found!!.serial)
    }

    @Test
    fun `검체 타입별 조회 테스트`() = runBlocking {
        // Given
        val type1 = sampleTypeRepository.save(SampleType.create(code = "TYPE1", name = "타입1", createdBy = "test"))
        val type2 = sampleTypeRepository.save(SampleType.create(code = "TYPE2", name = "타입2", createdBy = "test"))

        sampleRepository.save(Sample.create(serial = "S004", sampleTypeId = type1.id!!, createdBy = "test"))
        sampleRepository.save(Sample.create(serial = "S005", sampleTypeId = type1.id!!, createdBy = "test"))
        sampleRepository.save(Sample.create(serial = "S006", sampleTypeId = type2.id!!, createdBy = "test"))

        // When
        val type1Samples = sampleRepository.findBySampleTypeId(type1.id!!).toList()

        // Then
        assertTrue(type1Samples.size >= 2)
        assertTrue(type1Samples.all { it.sampleTypeId == type1.id })
    }

    @Test
    fun `검체 수정 테스트`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(code = "BLD004", name = "타입", createdBy = "test"))
        val sample = Sample.create(serial = "S007", sampleTypeId = sampleType.id!!, createdBy = "test")
        val saved = sampleRepository.save(sample)

        // When
        val updated = saved.update(
            collectedBy = "collector",
            note = "수정된 노트",
            updatedBy = "updater"
        )
        val result = sampleRepository.save(updated)

        // Then
        assertEquals(saved.id, result.id)
        assertEquals("collector", result.collectedBy)
        assertEquals("수정된 노트", result.note)
        assertEquals("updater", result.updatedBy)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `검체 삭제 테스트`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(code = "BLD005", name = "타입", createdBy = "test"))
        val sample = Sample.create(serial = "S008", sampleTypeId = sampleType.id!!, createdBy = "test")
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
        val sampleType = sampleTypeRepository.save(SampleType.create(code = "BLD006", name = "타입", createdBy = "test"))
        sampleRepository.save(Sample.create(serial = "S009", sampleTypeId = sampleType.id!!, createdBy = "test"))
        sampleRepository.save(Sample.create(serial = "S010", sampleTypeId = sampleType.id!!, createdBy = "test"))

        // When
        val samples = sampleRepository.findAll().toList()

        // Then
        assertTrue(samples.size >= 2)
        assertTrue(samples.any { it.serial == "S009" })
        assertTrue(samples.any { it.serial == "S010" })
    }

    @Test
    fun `중복 검체 일련번호 체크 테스트`() = runBlocking {
        // Given
        val sampleType = sampleTypeRepository.save(SampleType.create(code = "BLD007", name = "타입", createdBy = "test"))
        val serial = "S011"
        sampleRepository.save(Sample.create(serial = serial, sampleTypeId = sampleType.id!!, createdBy = "test"))

        // When
        val exists = sampleRepository.existsBySerial(serial)

        // Then
        assertTrue(exists)

        // 존재하지 않는 경우
        val notExists = sampleRepository.existsBySerial("NOTEXIST")
        assertFalse(notExists)
    }

    @Test
    fun `중복 검체 타입 코드 체크 테스트`() = runBlocking {
        // Given
        val code = "DUPCODE"
        sampleTypeRepository.save(SampleType.create(code = code, name = "중복테스트", createdBy = "test"))

        // When
        val exists = sampleTypeRepository.existsByCode(code)

        // Then
        assertTrue(exists)

        // 존재하지 않는 경우
        val notExists = sampleTypeRepository.existsByCode("NOTEXIST")
        assertFalse(notExists)
    }
}
