package com.gcgenome.rms.service

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.dao.UserServiceDao
import com.gcgenome.rms.data.Message
import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.User
import org.jooq.DSLContext
import org.jooq.exception.IntegrityConstraintViolationException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext,
    val userDao: UserDao
): OrganizationDao, UserServiceDao {
    val addSuccess = "추가 완료 되었습니다."
    val deleteSuccess = "삭제 완료 되었습니다."

    fun insertUserService(authority: String?, userId: String, serviceId: String): Mono<Message> {
        return withAdminAuthority(authority) {
            dslContext.dsl().insertUserService(userId, serviceId)
                .map { Message.toModelOrgSer(it, addSuccess) }
                .onErrorResume(this::handleException)
        }
    }

    fun insertOrganization(userId: String, authority: String?, dto: Organization): Mono<Message> {
        return withAdminAuthority(authority) {
            dslContext.dsl().insertOrganization(userId, dto).map {
                Message.toModelOrg(it, addSuccess)
            }
                .onErrorResume(this::handleException)
        }
    }


    fun insertUser(userId: String, authority: String?, dto: User): Mono<Message> {
        return withAdminAuthority(authority) {
            Mono.from(dslContext.transactionPublisher { trx ->
                trx.dsl().run {
                    userDao.insertUser(dto).flatMap { userRecord ->
                        insertUserToOrganization(userRecord).map {
                            Message.toModelUser(userRecord, addSuccess)
                        }
                    }
                }.onErrorResume(this::handleException)
            })
        }
    }

    fun deleteUser(userId: String, authority: String?): Mono<Message> {
        return withAdminAuthority(authority) {
            dslContext.dsl().deleteOrganization(userId)
                .then(userDao.deleteUser(userId)).map{ userRecord ->
                    Message.toModelUser(userRecord, deleteSuccess)
                }
                .onErrorResume(this::handleException)
        }
    }

    private fun withAdminAuthority(authority: String?, action: () -> Mono<Message>): Mono<Message> {
        return if (hasAdminAuthority(authority)) {
            action()
        } else {
            Mono.just(Message("권한이 없습니다."))
        }
    }
    private fun hasAdminAuthority(authority: String?): Boolean {
        return authority == "ADMIN"
    }

    private fun handleException(exception: Throwable): Mono<Message> {
        return when (exception) {
            is IntegrityConstraintViolationException -> Mono.just(Message("중복된 값이 존재합니다."))
            else -> Mono.just(Message("GC지놈 담당자에게 문의 바랍니다.").apply { error = exception.javaClass.name })
        }
    }
}