package com.idrsys.ailis.rms.shared.exception

/**
 * Database 관련 Exception 정의
 */

/**
 * 데이터베이스 제약 조건 위반
 */
class DatabaseConstraintViolationException : DatabaseException(
    "exception.database.constraint"
)

/**
 * Unique Key 중복
 */
class UniqueKeyViolationException(key: String) : DatabaseException(
    "exception.database.uniqueKey",
    key
)

/**
 * Foreign Key 제약 조건 위반
 */
class ForeignKeyViolationException : DatabaseException(
    "exception.database.foreignKey"
)

/**
 * 데이터베이스 연결 실패
 */
class DatabaseConnectionException : DatabaseException(
    "exception.database.connection"
)
