package com.idrsys.ailis.rms.adapter.persistence

import com.idrsys.ailis.rms.domain.model.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * R2dbcRequestRepository 통합 테스트
 *
 * 실제 PostgreSQL 연결 필요:
 * - docker-compose up -d postgres
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class R2dbcRequestRepositoryTest {

    @Autowired
    private lateinit var requestRepository: R2dbcRequestRepository

    @Autowired
    private lateinit var organizationRepository: R2dbcOrganizationRepository

    @Autowired
    private lateinit var patientRepository: R2dbcPatientRepository

    @Autowired
    private lateinit var sampleRepository: R2dbcSampleRepository

    @Autowired
    private lateinit var sampleTypeRepository: R2dbcSampleTypeRepository

    private lateinit var testOrganization: Organization
    private lateinit var testPatient: Patient
    private lateinit var testSample: Sample

    @BeforeEach
    fun setup() = runBlocking {
        // 테스트용 기관 생성
        testOrganization = organizationRepository.save(
            Organization.create(
                serial = "ORG_TEST",
                name = "테스트병원",
                createdBy = "test"
            )
        )

        // 테스트용 환자 생성
        testPatient = patientRepository.save(
            Patient.create(
                serial = "PAT_TEST",
                name = "테스트환자",
                sex = Sex.MALE,
                birth = "19900101",
                createdBy = "test"
            )
        )

        // 테스트용 검체 타입 및 검체 생성
        val sampleType = sampleTypeRepository.save(
            SampleType.create(code = "BLD_TEST", name = "혈액", createdBy = "test")
        )
        testSample = sampleRepository.save(
            Sample.create(
                serial = "SMP_TEST",
                sampleTypeId = sampleType.id!!,
                createdBy = "test"
            )
        )
    }

    @Test
    fun `의뢰 생성 테스트`() = runBlocking {
        // Given
        val request = Request.create(
            serial = "REQ001",
            requestDateFrom = LocalDate.of(2024, 1, 1),
            requestDateTo = LocalDate.of(2024, 1, 10),
            organizationId = testOrganization.id!!,
            patientId = testPatient.id!!,
            sampleId = testSample.id!!,
            createdBy = "test"
        )

        // When
        val saved = requestRepository.save(request)

        // Then
        assertNotNull(saved.id)
        assertEquals("REQ001", saved.serial)
        assertEquals(LocalDate.of(2024, 1, 1), saved.requestDateFrom)
        assertEquals(LocalDate.of(2024, 1, 10), saved.requestDateTo)
        assertEquals(testOrganization.id, saved.organizationId)
        assertEquals(testPatient.id, saved.patientId)
        assertEquals(testSample.id, saved.sampleId)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `의뢰 조회 테스트 - ID로 조회`() = runBlocking {
        // Given
        val request = Request.create(
            serial = "REQ002",
            requestDateFrom = LocalDate.now(),
            requestDateTo = LocalDate.now().plusDays(7),
            organizationId = testOrganization.id!!,
            patientId = testPatient.id!!,
            sampleId = testSample.id!!,
            createdBy = "test"
        )
        val saved = requestRepository.save(request)

        // When
        val found = requestRepository.findById(saved.id!!)

        // Then
        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertEquals("REQ002", found.serial)
    }

    @Test
    fun `의뢰 조회 테스트 - 일련번호로 조회`() = runBlocking {
        // Given
        val request = Request.create(
            serial = "REQ003",
            requestDateFrom = LocalDate.now(),
            requestDateTo = LocalDate.now().plusDays(7),
            organizationId = testOrganization.id!!,
            patientId = testPatient.id!!,
            sampleId = testSample.id!!,
            createdBy = "test"
        )
        requestRepository.save(request)

        // When
        val found = requestRepository.findBySerial("REQ003")

        // Then
        assertNotNull(found)
        assertEquals("REQ003", found!!.serial)
    }

    @Test
    fun `의뢰 수정 테스트`() = runBlocking {
        // Given
        val request = Request.create(
            serial = "REQ004",
            requestDateFrom = LocalDate.now(),
            requestDateTo = LocalDate.now().plusDays(7),
            organizationId = testOrganization.id!!,
            patientId = testPatient.id!!,
            sampleId = testSample.id!!,
            createdBy = "test"
        )
        val saved = requestRepository.save(request)

        // When
        val updated = saved.update(
            testContent = "업데이트된 검사 내용",
            status = "COMPLETED",
            updatedBy = "updater"
        )
        val result = requestRepository.save(updated)

        // Then
        assertEquals(saved.id, result.id)
        assertEquals("업데이트된 검사 내용", result.testContent)
        assertEquals("COMPLETED", result.status)
        assertEquals("updater", result.updatedBy)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `의뢰 삭제 테스트`() = runBlocking {
        // Given
        val request = Request.create(
            serial = "REQ005",
            requestDateFrom = LocalDate.now(),
            requestDateTo = LocalDate.now().plusDays(7),
            organizationId = testOrganization.id!!,
            patientId = testPatient.id!!,
            sampleId = testSample.id!!,
            createdBy = "test"
        )
        val saved = requestRepository.save(request)

        // When
        val deleted = requestRepository.deleteById(saved.id!!)

        // Then
        assertTrue(deleted)

        // Verify
        val found = requestRepository.findById(saved.id!!)
        assertNull(found)
    }

    @Test
    fun `의뢰 목록 조회 테스트`() = runBlocking {
        // Given
        requestRepository.save(
            Request.create(
                serial = "REQ006",
                requestDateFrom = LocalDate.now(),
                requestDateTo = LocalDate.now().plusDays(7),
                organizationId = testOrganization.id!!,
                patientId = testPatient.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )
        requestRepository.save(
            Request.create(
                serial = "REQ007",
                requestDateFrom = LocalDate.now(),
                requestDateTo = LocalDate.now().plusDays(7),
                organizationId = testOrganization.id!!,
                patientId = testPatient.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )

        // When
        val requests = requestRepository.findAll().toList()

        // Then
        assertTrue(requests.size >= 2)
        assertTrue(requests.any { it.serial == "REQ006" })
        assertTrue(requests.any { it.serial == "REQ007" })
    }

    @Test
    fun `기관별 의뢰 조회 테스트`() = runBlocking {
        // Given
        val org2 = organizationRepository.save(
            Organization.create(serial = "ORG_TEST2", name = "병원2", createdBy = "test")
        )

        requestRepository.save(
            Request.create(
                serial = "REQ008",
                requestDateFrom = LocalDate.now(),
                requestDateTo = LocalDate.now().plusDays(7),
                organizationId = testOrganization.id!!,
                patientId = testPatient.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )
        requestRepository.save(
            Request.create(
                serial = "REQ009",
                requestDateFrom = LocalDate.now(),
                requestDateTo = LocalDate.now().plusDays(7),
                organizationId = org2.id!!,
                patientId = testPatient.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )

        // When
        val orgRequests = requestRepository.findByOrganizationId(testOrganization.id!!).toList()

        // Then
        assertTrue(orgRequests.isNotEmpty())
        assertTrue(orgRequests.all { it.organizationId == testOrganization.id })
    }

    @Test
    fun `환자별 의뢰 조회 테스트`() = runBlocking {
        // Given
        val patient2 = patientRepository.save(
            Patient.create(serial = "PAT_TEST2", name = "환자2", sex = Sex.FEMALE, birth = "19950101", createdBy = "test")
        )

        requestRepository.save(
            Request.create(
                serial = "REQ010",
                requestDateFrom = LocalDate.now(),
                requestDateTo = LocalDate.now().plusDays(7),
                organizationId = testOrganization.id!!,
                patientId = testPatient.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )
        requestRepository.save(
            Request.create(
                serial = "REQ011",
                requestDateFrom = LocalDate.now(),
                requestDateTo = LocalDate.now().plusDays(7),
                organizationId = testOrganization.id!!,
                patientId = patient2.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )

        // When
        val patientRequests = requestRepository.findByPatientId(testPatient.id!!).toList()

        // Then
        assertTrue(patientRequests.isNotEmpty())
        assertTrue(patientRequests.all { it.patientId == testPatient.id })
    }

    @Test
    fun `의뢰일 범위로 조회 테스트`() = runBlocking {
        // Given
        requestRepository.save(
            Request.create(
                serial = "REQ012",
                requestDateFrom = LocalDate.of(2024, 1, 1),
                requestDateTo = LocalDate.of(2024, 1, 10),
                organizationId = testOrganization.id!!,
                patientId = testPatient.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )
        requestRepository.save(
            Request.create(
                serial = "REQ013",
                requestDateFrom = LocalDate.of(2024, 6, 1),
                requestDateTo = LocalDate.of(2024, 6, 10),
                organizationId = testOrganization.id!!,
                patientId = testPatient.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )

        // When
        val rangeRequests = requestRepository.findByRequestDateBetween(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 3, 31)
        ).toList()

        // Then
        assertTrue(rangeRequests.isNotEmpty())
        assertTrue(rangeRequests.all {
            it.requestDateFrom >= LocalDate.of(2024, 1, 1) ||
            it.requestDateTo <= LocalDate.of(2024, 3, 31)
        })
    }

    @Test
    fun `중복 일련번호 체크 테스트`() = runBlocking {
        // Given
        val serial = "REQ014"
        requestRepository.save(
            Request.create(
                serial = serial,
                requestDateFrom = LocalDate.now(),
                requestDateTo = LocalDate.now().plusDays(7),
                organizationId = testOrganization.id!!,
                patientId = testPatient.id!!,
                sampleId = testSample.id!!,
                createdBy = "test"
            )
        )

        // When
        val exists = requestRepository.existsBySerial(serial)

        // Then
        assertTrue(exists)

        // 존재하지 않는 경우
        val notExists = requestRepository.existsBySerial("NOTEXIST")
        assertFalse(notExists)
    }
}
