package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Organization
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

/**
 * R2dbcOrganizationRepository 통합 테스트
 *
 * 실제 PostgreSQL 연결 필요:
 * - docker-compose up -d postgres
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class R2dbcOrganizationRepositoryTest {

    @Autowired
    private lateinit var repository: R2dbcOrganizationRepository

    @Test
    fun `기관 생성 테스트`() = runBlocking {
        // Given
        val organization = Organization.create(
            serial = "TEST001",
            name = "테스트 병원",
            type = "HOSPITAL",
            createdBy = "test"
        )

        // When
        val saved = repository.save(organization)

        // Then
        assertNotNull(saved.id)
        assertEquals("TEST001", saved.serial)
        assertEquals("테스트 병원", saved.name)
        assertEquals("HOSPITAL", saved.type)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `기관 조회 테스트 - ID로 조회`() = runBlocking {
        // Given
        val organization = Organization.create(
            serial = "TEST002",
            name = "테스트 클리닉",
            createdBy = "test"
        )
        val saved = repository.save(organization)

        // When
        val found = repository.findById(saved.id!!)

        // Then
        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertEquals("TEST002", found.serial)
        assertEquals("테스트 클리닉", found.name)
    }

    @Test
    fun `기관 조회 테스트 - 일련번호로 조회`() = runBlocking {
        // Given
        val organization = Organization.create(
            serial = "TEST003",
            name = "테스트 의원",
            createdBy = "test"
        )
        repository.save(organization)

        // When
        val found = repository.findBySerial("TEST003")

        // Then
        assertNotNull(found)
        assertEquals("TEST003", found!!.serial)
        assertEquals("테스트 의원", found.name)
    }

    @Test
    fun `기관 수정 테스트`() = runBlocking {
        // Given
        val organization = Organization.create(
            serial = "TEST004",
            name = "원래 이름",
            createdBy = "test"
        )
        val saved = repository.save(organization)

        // When
        val updated = saved.update(
            name = "변경된 이름",
            type = "CLINIC",
            updatedBy = "updater"
        )
        val result = repository.save(updated)

        // Then
        assertEquals(saved.id, result.id)
        assertEquals("변경된 이름", result.name)
        assertEquals("CLINIC", result.type)
        assertEquals("updater", result.updatedBy)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `기관 삭제 테스트`() = runBlocking {
        // Given
        val organization = Organization.create(
            serial = "TEST005",
            name = "삭제될 기관",
            createdBy = "test"
        )
        val saved = repository.save(organization)

        // When
        val deleted = repository.deleteById(saved.id!!)

        // Then
        assertTrue(deleted)

        // Verify
        val found = repository.findById(saved.id!!)
        assertNull(found)
    }

    @Test
    fun `기관 목록 조회 테스트`() = runBlocking {
        // Given
        val org1 = Organization.create(serial = "TEST006", name = "기관1", createdBy = "test")
        val org2 = Organization.create(serial = "TEST007", name = "기관2", createdBy = "test")
        repository.save(org1)
        repository.save(org2)

        // When
        val organizations = repository.findAll().toList()

        // Then
        assertTrue(organizations.size >= 2)
        assertTrue(organizations.any { it.serial == "TEST006" })
        assertTrue(organizations.any { it.serial == "TEST007" })
    }

    @Test
    fun `기관명으로 검색 테스트`() = runBlocking {
        // Given
        repository.save(Organization.create(serial = "TEST008", name = "서울대학교병원", createdBy = "test"))
        repository.save(Organization.create(serial = "TEST009", name = "서울아산병원", createdBy = "test"))
        repository.save(Organization.create(serial = "TEST010", name = "부산대학교병원", createdBy = "test"))

        // When
        val results = repository.findByName("서울").toList()

        // Then
        assertTrue(results.size >= 2)
        assertTrue(results.all { it.name.contains("서울") })
    }

    @Test
    fun `중복 일련번호 체크 테스트`() = runBlocking {
        // Given
        val serial = "TEST011"
        repository.save(Organization.create(serial = serial, name = "기관", createdBy = "test"))

        // When
        val exists = repository.existsBySerial(serial)

        // Then
        assertTrue(exists)

        // 존재하지 않는 경우
        val notExists = repository.existsBySerial("NOTEXIST")
        assertFalse(notExists)
    }
}
