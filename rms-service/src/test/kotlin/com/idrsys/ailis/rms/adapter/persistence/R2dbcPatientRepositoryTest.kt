package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.Patient
import com.idrsys.ailis.rms.domain.model.Sex
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
 * R2dbcPatientRepository 통합 테스트
 *
 * 실제 PostgreSQL 연결 필요:
 * - docker-compose up -d postgres
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class R2dbcPatientRepositoryTest {

    @Autowired
    private lateinit var repository: R2dbcPatientRepository

    @Test
    fun `환자 생성 테스트`() = runBlocking {
        // Given
        val patient = Patient.create(
            serial = "P001",
            name = "홍길동",
            sex = Sex.MALE,
            birth = LocalDate.of(1990, 1, 1),
            createdBy = "test"
        )

        // When
        val saved = repository.save(patient)

        // Then
        assertNotNull(saved.id)
        assertEquals("P001", saved.serial)
        assertEquals("홍길동", saved.name)
        assertEquals(Sex.MALE, saved.sex)
        assertEquals(LocalDate.of(1990, 1, 1), saved.birth)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `환자 조회 테스트 - ID로 조회`() = runBlocking {
        // Given
        val patient = Patient.create(
            serial = "P002",
            name = "김영희",
            sex = Sex.FEMALE,
            birth = LocalDate.of(1995, 5, 20),
            createdBy = "test"
        )
        val saved = repository.save(patient)

        // When
        val found = repository.findById(saved.id!!)

        // Then
        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertEquals("P002", found.serial)
        assertEquals("김영희", found.name)
        assertEquals(Sex.FEMALE, found.sex)
    }

    @Test
    fun `환자 조회 테스트 - 일련번호로 조회`() = runBlocking {
        // Given
        val patient = Patient.create(
            serial = "P003",
            name = "박철수",
            sex = Sex.MALE,
            birth = LocalDate.of(1988, 3, 15),
            createdBy = "test"
        )
        repository.save(patient)

        // When
        val found = repository.findBySerial("P003")

        // Then
        assertNotNull(found)
        assertEquals("P003", found!!.serial)
        assertEquals("박철수", found.name)
    }

    @Test
    fun `환자 수정 테스트`() = runBlocking {
        // Given
        val patient = Patient.create(
            serial = "P004",
            name = "이민수",
            sex = Sex.MALE,
            birth = LocalDate.of(1992, 7, 10),
            createdBy = "test"
        )
        val saved = repository.save(patient)

        // When
        val updated = saved.update(
            name = "이민수(변경)",
            phone = "010-1234-5678",
            updatedBy = "updater"
        )
        val result = repository.save(updated)

        // Then
        assertEquals(saved.id, result.id)
        assertEquals("이민수(변경)", result.name)
        assertEquals("010-1234-5678", result.phone)
        assertEquals("updater", result.updatedBy)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `환자 삭제 테스트`() = runBlocking {
        // Given
        val patient = Patient.create(
            serial = "P005",
            name = "최지영",
            sex = Sex.FEMALE,
            birth = LocalDate.of(1993, 8, 25),
            createdBy = "test"
        )
        val saved = repository.save(patient)

        // When
        val deleted = repository.deleteById(saved.id!!)

        // Then
        assertTrue(deleted)

        // Verify
        val found = repository.findById(saved.id!!)
        assertNull(found)
    }

    @Test
    fun `환자 목록 조회 테스트`() = runBlocking {
        // Given
        val patient1 = Patient.create(serial = "P006", name = "환자1", sex = Sex.MALE, birth = LocalDate.of(1990, 1, 1), createdBy = "test")
        val patient2 = Patient.create(serial = "P007", name = "환자2", sex = Sex.FEMALE, birth = LocalDate.of(1991, 2, 2), createdBy = "test")
        repository.save(patient1)
        repository.save(patient2)

        // When
        val patients = repository.findAll().toList()

        // Then
        assertTrue(patients.size >= 2)
        assertTrue(patients.any { it.serial == "P006" })
        assertTrue(patients.any { it.serial == "P007" })
    }

    @Test
    fun `환자명으로 검색 테스트`() = runBlocking {
        // Given
        repository.save(Patient.create(serial = "P008", name = "서울홍길동", sex = Sex.MALE, birth = LocalDate.of(1990, 1, 1), createdBy = "test"))
        repository.save(Patient.create(serial = "P009", name = "서울김영희", sex = Sex.FEMALE, birth = LocalDate.of(1991, 2, 2), createdBy = "test"))
        repository.save(Patient.create(serial = "P010", name = "부산박철수", sex = Sex.MALE, birth = LocalDate.of(1992, 3, 3), createdBy = "test"))

        // When
        val results = repository.findByName("서울").toList()

        // Then
        assertTrue(results.size >= 2)
        assertTrue(results.all { it.name.contains("서울") })
    }

    @Test
    fun `중복 일련번호 체크 테스트`() = runBlocking {
        // Given
        val serial = "P011"
        repository.save(Patient.create(serial = serial, name = "테스트환자", sex = Sex.MALE, birth = LocalDate.of(1990, 1, 1), createdBy = "test"))

        // When
        val exists = repository.existsBySerial(serial)

        // Then
        assertTrue(exists)

        // 존재하지 않는 경우
        val notExists = repository.existsBySerial("NOTEXIST")
        assertFalse(notExists)
    }
}
