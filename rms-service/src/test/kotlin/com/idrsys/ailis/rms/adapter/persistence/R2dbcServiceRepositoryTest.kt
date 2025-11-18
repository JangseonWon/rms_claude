package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Service
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

/**
 * R2dbcServiceRepository 통합 테스트
 *
 * 실제 PostgreSQL 연결 필요:
 * - docker-compose up -d postgres
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class R2dbcServiceRepositoryTest {

    @Autowired
    private lateinit var repository: R2dbcServiceRepository

    @Test
    fun `서비스 생성 테스트`() = runBlocking {
        // Given
        val service = Service.create(
            serial = "SRV001",
            name = "전장유전체분석",
            description = "WGS 분석 서비스",
            price = 1000000L,
            categoryId = 1L,
            createdBy = "test"
        )

        // When
        val saved = repository.save(service)

        // Then
        assertNotNull(saved.id)
        assertEquals("SRV001", saved.serial)
        assertEquals("전장유전체분석", saved.name)
        assertEquals("WGS 분석 서비스", saved.description)
        assertEquals(1000000L, saved.price)
        assertEquals(1L, saved.categoryId)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `서비스 조회 테스트 - ID로 조회`() = runBlocking {
        // Given
        val service = Service.create(
            serial = "SRV002",
            name = "전사체분석",
            description = "RNA-Seq 분석",
            price = 500000L,
            createdBy = "test"
        )
        val saved = repository.save(service)

        // When
        val found = repository.findById(saved.id!!)

        // Then
        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertEquals("SRV002", found.serial)
        assertEquals("전사체분석", found.name)
    }

    @Test
    fun `서비스 조회 테스트 - 일련번호로 조회`() = runBlocking {
        // Given
        val service = Service.create(
            serial = "SRV003",
            name = "후성유전체분석",
            description = "Methylation 분석",
            price = 800000L,
            createdBy = "test"
        )
        repository.save(service)

        // When
        val found = repository.findBySerial("SRV003")

        // Then
        assertNotNull(found)
        assertEquals("SRV003", found!!.serial)
        assertEquals("후성유전체분석", found.name)
    }

    @Test
    fun `서비스 수정 테스트`() = runBlocking {
        // Given
        val service = Service.create(
            serial = "SRV004",
            name = "유전자패널분석",
            description = "Targeted Sequencing",
            price = 300000L,
            createdBy = "test"
        )
        val saved = repository.save(service)

        // When
        val updated = saved.update(
            name = "유전자패널분석 (업데이트)",
            price = 350000L,
            updatedBy = "updater"
        )
        val result = repository.save(updated)

        // Then
        assertEquals(saved.id, result.id)
        assertEquals("유전자패널분석 (업데이트)", result.name)
        assertEquals(350000L, result.price)
        assertEquals("updater", result.updatedBy)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `서비스 삭제 테스트`() = runBlocking {
        // Given
        val service = Service.create(
            serial = "SRV005",
            name = "삭제테스트서비스",
            createdBy = "test"
        )
        val saved = repository.save(service)

        // When
        val deleted = repository.deleteById(saved.id!!)

        // Then
        assertTrue(deleted)

        // Verify
        val found = repository.findById(saved.id!!)
        assertNull(found)
    }

    @Test
    fun `서비스 목록 조회 테스트`() = runBlocking {
        // Given
        repository.save(Service.create(serial = "SRV006", name = "서비스1", createdBy = "test"))
        repository.save(Service.create(serial = "SRV007", name = "서비스2", createdBy = "test"))

        // When
        val services = repository.findAll().toList()

        // Then
        assertTrue(services.size >= 2)
        assertTrue(services.any { it.serial == "SRV006" })
        assertTrue(services.any { it.serial == "SRV007" })
    }

    @Test
    fun `카테고리별 서비스 조회 테스트`() = runBlocking {
        // Given
        repository.save(Service.create(serial = "SRV008", name = "서비스A", categoryId = 1L, createdBy = "test"))
        repository.save(Service.create(serial = "SRV009", name = "서비스B", categoryId = 1L, createdBy = "test"))
        repository.save(Service.create(serial = "SRV010", name = "서비스C", categoryId = 2L, createdBy = "test"))

        // When
        val category1Services = repository.findByCategoryId(1L).toList()

        // Then
        assertTrue(category1Services.size >= 2)
        assertTrue(category1Services.all { it.categoryId == 1L })
    }

    @Test
    fun `중복 일련번호 체크 테스트`() = runBlocking {
        // Given
        val serial = "SRV011"
        repository.save(Service.create(serial = serial, name = "테스트서비스", createdBy = "test"))

        // When
        val exists = repository.existsBySerial(serial)

        // Then
        assertTrue(exists)

        // 존재하지 않는 경우
        val notExists = repository.existsBySerial("NOTEXIST")
        assertFalse(notExists)
    }

    @Test
    fun `서비스 생성 - 가격 없이`() = runBlocking {
        // Given
        val service = Service.create(
            serial = "SRV012",
            name = "무료서비스",
            description = "가격 정보 없음",
            createdBy = "test"
        )

        // When
        val saved = repository.save(service)

        // Then
        assertNotNull(saved.id)
        assertEquals("SRV012", saved.serial)
        assertNull(saved.price)
    }

    @Test
    fun `서비스 생성 - 카테고리 없이`() = runBlocking {
        // Given
        val service = Service.create(
            serial = "SRV013",
            name = "미분류서비스",
            price = 100000L,
            createdBy = "test"
        )

        // When
        val saved = repository.save(service)

        // Then
        assertNotNull(saved.id)
        assertEquals("SRV013", saved.serial)
        assertNull(saved.categoryId)
    }

    @Test
    fun `서비스 수정 - 가격만 변경`() = runBlocking {
        // Given
        val service = Service.create(
            serial = "SRV014",
            name = "가격변경테스트",
            price = 100000L,
            createdBy = "test"
        )
        val saved = repository.save(service)

        // When
        val updated = saved.update(price = 200000L, updatedBy = "updater")
        val result = repository.save(updated)

        // Then
        assertEquals(saved.id, result.id)
        assertEquals(200000L, result.price)
        assertEquals("가격변경테스트", result.name) // 이름은 그대로
    }
}
