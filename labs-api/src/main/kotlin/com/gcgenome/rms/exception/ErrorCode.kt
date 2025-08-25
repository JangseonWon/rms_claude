package com.gcgenome.rms.exception

enum class ErrorCode {
    VALIDATION_FAILED,     // 422: 바디/비즈니스 검증 실패
    DUPLICATE_KEY,         // 409: UK 충돌
    CONFLICT,              // 409: 일반 충돌(기본값)
    DELETE_NOT_ALLOWED,    // 409: 삭제 금지(잠금/상태)
    EDIT_NOT_ALLOWED,      // 409: 수정 금지(잠금/상태)
    NOT_FOUND,             // 404: 리소스 없음
    PERMISSION_DENIED,     // 403: 권한 없음 (필요 시)
    PRECONDITION_FAILED,   // 412: ETag 등 전제조건 실패 (필요 시)
    RATE_LIMITED           // 429: 필요 시
}