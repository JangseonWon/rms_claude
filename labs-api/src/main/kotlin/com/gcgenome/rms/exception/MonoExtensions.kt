package com.gcgenome.rms.exception

import com.gcgenome.rms.data.FieldError
import org.jooq.exception.IntegrityConstraintViolationException
import reactor.core.publisher.Mono

/** status: 422
 * DB에 매핑된 기관 등록 정보(organization.serial, user_sample_type.serial 등) 없는경우
 * 필수 의뢰정보가 누락된 경우
 * */
fun <T> Mono<T>.orUnprocessable(fieldErrors: List<FieldError> = emptyList()): Mono<T> =
    this.switchIfEmpty(Mono.error(UnprocessableEntityException(fieldErrors)))

/** status: 409
 * UK 중복이 발생되었을 때
 * */
fun <T> Mono<T>.mapUniqueViolation(
    constraint: String,
    fieldErrors: List<FieldError> = emptyList(),
    code: ErrorCode = ErrorCode.CONFLICT,
    rejectValue: Any? = null): Mono<T> =
    this.onErrorMap { e ->
        if (e is IntegrityConstraintViolationException &&
            (e.message?.contains(constraint) == true))
            ConflictException(fieldErrors, rejectValue, code)
        else e
    }
/** status: 404
 * 조회값이 없을 때
 * */
fun <T> Mono<T>.orNotFound(
    fieldErrors: List<FieldError> = emptyList(),
): Mono<T> =
    this.switchIfEmpty(Mono.error(NotFoundException(fieldErrors)))

/** status: 400
 * 입력이 비었으면
 * */
fun <T> T?.orBadRequest(message: String): T =
    this ?: throw IllegalArgumentException(message)


