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
 * R2dbcExtensionRepository 통합 테스트
 *
 * 실제 PostgreSQL 연결 필요:
 * - docker-compose up -d postgres
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class R2dbcExtensionRepositoryTest {

    @Autowired
    private lateinit var extensionRepository: R2dbcExtensionRepository

    @Autowired
    private lateinit var requestExtensionRepository: R2dbcRequestExtensionRepository

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

    private lateinit var testRequest: Request

    @BeforeEach
    fun setup() = runBlocking {
        // 테스트용 의뢰 데이터 준비
        val org = organizationRepository.save(
            Organization.create(serial = "ORG_EXT", name = "확장테스트병원", createdBy = "test")
        )
        val patient = patientRepository.save(
            Patient.create(serial = "PAT_EXT", name = "확장테스트환자", sex = Sex.MALE, birth = "19900101", createdBy = "test")
        )
        val sampleType = sampleTypeRepository.save(
            SampleType.create(serial = "BLD_EXT", name = "혈액", createdBy = "test")
        )
        val sample = sampleRepository.save(
            Sample.create(
                count = 1,
                samplingOn = LocalDate.now(),
                sampleTypeId = sampleType.id!!,
                createdBy = "test"
            )
        )
        testRequest = requestRepository.save(
            Request.create(
                serial = "REQ_EXT",
                requestDateFrom = LocalDate.now(),
                requestDateTo = LocalDate.now().plusDays(7),
                organizationId = org.id!!,
                patientId = patient.id!!,
                sampleId = sample.id!!,
                createdBy = "test"
            )
        )
    }

    @Test
    fun `확장항목 생성 테스트`() = runBlocking {
        // Given
        val extension = Extension.create(
            code = "EXT001",
            name = "추가검사1",
            createdBy = "test"
        )

        // When
        val saved = extensionRepository.save(extension)

        // Then
        assertNotNull(saved.id)
        assertEquals("EXT001", saved.code)
        assertEquals("추가검사1", saved.name)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `확장항목 조회 테스트 - 코드로 조회`() = runBlocking {
        // Given
        val extension = Extension.create(code = "EXT002", name = "추가검사2", createdBy = "test")
        extensionRepository.save(extension)

        // When
        val found = extensionRepository.findByCode("EXT002")

        // Then
        assertNotNull(found)
        assertEquals("EXT002", found!!.code)
        assertEquals("추가검사2", found.name)
    }

    @Test
    fun `확장항목 목록 조회 테스트`() = runBlocking {
        // Given
        extensionRepository.save(Extension.create(code = "EXT003", name = "확장1", createdBy = "test"))
        extensionRepository.save(Extension.create(code = "EXT004", name = "확장2", createdBy = "test"))

        // When
        val extensions = extensionRepository.findAll().toList()

        // Then
        assertTrue(extensions.size >= 2)
        assertTrue(extensions.any { it.code == "EXT003" })
        assertTrue(extensions.any { it.code == "EXT004" })
    }

    @Test
    fun `확장항목 수정 테스트`() = runBlocking {
        // Given
        val extension = Extension.create(code = "EXT005", name = "원래이름", createdBy = "test")
        val saved = extensionRepository.save(extension)

        // When
        val updated = saved.update(
            name = "변경된이름",
            description = "설명추가",
            updatedBy = "updater"
        )
        val result = extensionRepository.save(updated)

        // Then
        assertEquals(saved.id, result.id)
        assertEquals("변경된이름", result.name)
        assertEquals("설명추가", result.description)
        assertEquals("updater", result.updatedBy)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `확장항목 삭제 테스트`() = runBlocking {
        // Given
        val extension = Extension.create(code = "EXT006", name = "삭제테스트", createdBy = "test")
        val saved = extensionRepository.save(extension)

        // When
        val deleted = extensionRepository.deleteById(saved.id!!)

        // Then
        assertTrue(deleted)

        // Verify
        val found = extensionRepository.findById(saved.id!!)
        assertNull(found)
    }

    @Test
    fun `중복 확장항목 코드 체크 테스트`() = runBlocking {
        // Given
        val code = "DUPEXT"
        extensionRepository.save(Extension.create(code = code, name = "중복테스트", createdBy = "test"))

        // When
        val exists = extensionRepository.existsByCode(code)

        // Then
        assertTrue(exists)

        // 존재하지 않는 경우
        val notExists = extensionRepository.existsByCode("NOTEXIST")
        assertFalse(notExists)
    }

    @Test
    fun `의뢰-확장항목 연결 테스트`() = runBlocking {
        // Given
        val extension = extensionRepository.save(
            Extension.create(code = "EXT007", name = "연결테스트", createdBy = "test")
        )

        val requestExtension = RequestExtension.create(
            requestId = testRequest.id!!,
            extensionCode = extension.code,
            value = "테스트값",
            createdBy = "test"
        )

        // When
        val saved = requestExtensionRepository.save(requestExtension)

        // Then
        assertNotNull(saved.id)
        assertEquals(testRequest.id, saved.requestId)
        assertEquals(extension.code, saved.extensionCode)
        assertEquals("테스트값", saved.value)
        assertEquals("test", saved.createdBy)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun `의뢰별 확장항목 조회 테스트`() = runBlocking {
        // Given
        val ext1 = extensionRepository.save(Extension.create(code = "EXT008", name = "확장8", createdBy = "test"))
        val ext2 = extensionRepository.save(Extension.create(code = "EXT009", name = "확장9", createdBy = "test"))

        requestExtensionRepository.save(
            RequestExtension.create(requestId = testRequest.id!!, extensionCode = ext1.code, value = "값1", createdBy = "test")
        )
        requestExtensionRepository.save(
            RequestExtension.create(requestId = testRequest.id!!, extensionCode = ext2.code, value = "값2", createdBy = "test")
        )

        // When
        val requestExtensions = requestExtensionRepository.findByRequestId(testRequest.id!!).toList()

        // Then
        assertTrue(requestExtensions.size >= 2)
        assertTrue(requestExtensions.all { it.requestId == testRequest.id })
        assertTrue(requestExtensions.any { it.extensionCode == ext1.code })
        assertTrue(requestExtensions.any { it.extensionCode == ext2.code })
    }

    @Test
    fun `의뢰 ID와 코드로 확장항목 조회 테스트`() = runBlocking {
        // Given
        val extension = extensionRepository.save(
            Extension.create(code = "EXT010", name = "확장10", createdBy = "test")
        )

        requestExtensionRepository.save(
            RequestExtension.create(
                requestId = testRequest.id!!,
                extensionCode = extension.code,
                value = "조회테스트값",
                createdBy = "test"
            )
        )

        // When
        val found = requestExtensionRepository.findByRequestIdAndCode(testRequest.id!!, extension.code)

        // Then
        assertNotNull(found)
        assertEquals(testRequest.id, found!!.requestId)
        assertEquals(extension.code, found.extensionCode)
        assertEquals("조회테스트값", found.value)
    }

    @Test
    fun `의뢰-확장항목 연결 삭제 테스트`() = runBlocking {
        // Given
        val extension = extensionRepository.save(
            Extension.create(code = "EXT011", name = "확장11", createdBy = "test")
        )
        val requestExtension = requestExtensionRepository.save(
            RequestExtension.create(
                requestId = testRequest.id!!,
                extensionCode = extension.code,
                value = "삭제테스트값",
                createdBy = "test"
            )
        )

        // When
        val deleted = requestExtensionRepository.deleteById(requestExtension.id!!)

        // Then
        assertTrue(deleted)

        // Verify
        val found = requestExtensionRepository.findById(requestExtension.id!!)
        assertNull(found)
    }

    @Test
    fun `의뢰 ID로 모든 확장항목 삭제 테스트`() = runBlocking {
        // Given
        val ext1 = extensionRepository.save(Extension.create(code = "EXT012", name = "확장12", createdBy = "test"))
        val ext2 = extensionRepository.save(Extension.create(code = "EXT013", name = "확장13", createdBy = "test"))

        requestExtensionRepository.save(
            RequestExtension.create(requestId = testRequest.id!!, extensionCode = ext1.code, value = "값1", createdBy = "test")
        )
        requestExtensionRepository.save(
            RequestExtension.create(requestId = testRequest.id!!, extensionCode = ext2.code, value = "값2", createdBy = "test")
        )

        // When
        val deletedCount = requestExtensionRepository.deleteByRequestId(testRequest.id!!)

        // Then
        assertTrue(deletedCount >= 2)

        // Verify
        val remaining = requestExtensionRepository.findByRequestId(testRequest.id!!).toList()
        assertTrue(remaining.isEmpty())
    }

    @Test
    fun `확장항목 값 다양성 테스트`() = runBlocking {
        // Given
        val extension = extensionRepository.save(
            Extension.create(code = "EXT014", name = "다양성테스트", createdBy = "test")
        )

        // When - 여러 값 저장
        requestExtensionRepository.save(
            RequestExtension.create(
                requestId = testRequest.id!!,
                extensionCode = extension.code,
                value = "긴 문자열 값 테스트입니다. 여러 데이터를 포함할 수 있습니다.",
                createdBy = "test"
            )
        )

        // Then
        val found = requestExtensionRepository.findByRequestIdAndCode(testRequest.id!!, extension.code)
        assertNotNull(found)
        assertTrue(found!!.value.length > 10)
    }
}
