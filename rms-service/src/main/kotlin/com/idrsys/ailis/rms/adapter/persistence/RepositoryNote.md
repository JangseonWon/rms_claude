# Repository 구현 가이드

## 구현 패턴

모든 Repository는 R2dbcOrganizationRepository와 동일한 패턴을 따릅니다:

1. **DatabaseClient 사용**: R2DBC의 DatabaseClient를 활용한 쿼리 실행
2. **Row Mapper**: SQL Row를 Domain 모델로 변환
3. **Null 안전성**: bindNullable 확장 함수 사용
4. **Reactive**: Flow와 Coroutine 사용

## TODO: 구현 필요한 Repository

### 1. R2dbcPatientRepository
- `save(patient)`: INSERT/UPDATE
- `findById(id)`: ID로 조회
- `findBySerial(serial)`: 일련번호로 조회
- `findByName(name)`: 이름으로 검색 (LIKE)
- `findAll()`: 전체 조회
- `deleteById(id)`: 삭제
- `existsBySerial(serial)`: 존재 여부

### 2. R2dbcSampleRepository & R2dbcSampleTypeRepository
- SampleType 먼저 조회/생성 후 Sample 저장
- JOIN 쿼리로 SampleType 함께 조회

### 3. R2dbcRequestRepository
- 복잡한 JOIN 쿼리 필요
- Organization, Patient, Sample 함께 조회
- 날짜 범위 검색 (findByDateRange)

### 4. R2dbcExtensionRepository & R2dbcRequestExtensionRepository
- Extension 메타데이터 관리
- RequestExtension 값 저장

## 간소화 방안

실제 구현 시:
- Spring Data R2DBC의 `R2dbcEntityTemplate` 사용 고려
- jOOQ DSL 사용으로 타입 안전성 확보
- Repository 추상 클래스로 공통 로직 추출

## 예시 코드 (간략화)

```kotlin
@Repository
class R2dbcPatientRepository(
    private val databaseClient: DatabaseClient
) : PatientRepository {

    override suspend fun save(patient: Patient): Patient {
        // INSERT 또는 UPDATE 로직
        // R2dbcOrganizationRepository 패턴 참조
    }

    override suspend fun findById(id: Long): Patient? {
        val sql = "SELECT * FROM patients WHERE id = :id"
        return databaseClient.sql(sql)
            .bind("id", id)
            .map { row -> rowToPatient(row) }
            .awaitOneOrNull()
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
}
```

## 주의사항

1. **Null 처리**: PostgreSQL의 NULL과 Kotlin의 null 매핑 주의
2. **타입 변환**: java.lang.Long → Kotlin Long 변환
3. **Enum 매핑**: Sex enum은 String code로 저장
4. **LocalDate/LocalDateTime**: JDBC와 R2DBC 타입 차이 주의
5. **Transaction**: @Transactional은 Service 계층에서 처리
