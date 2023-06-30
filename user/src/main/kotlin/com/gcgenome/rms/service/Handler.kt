package com.gcgenome.rms.service

import com.gcgenome.rms.dao.OrganizationDao
import com.gcgenome.rms.dao.OrganizationServiceDao
import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.data.Message
import com.gcgenome.rms.data.Organization
import com.gcgenome.rms.data.User
import org.jooq.DSLContext
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("com.gcgenome.rms.order.Handler")
class Handler(
    val dslContext: DSLContext,
    val userDao: UserDao
): OrganizationDao, OrganizationServiceDao {
    val success = "추가 완료 되었습니다."

    fun insertOrganizationService(authority: String?, organizationId: String, serviceId: String, userId: String): Mono<Message> {
        return withAdminAuthority(authority) {
            dslContext.dsl().insertOrganizationService(organizationId, serviceId, userId)
                .map { Message.toModelOrgSer(it, success) }
                .onErrorResume(DuplicateKeyException::class.java) {
                    Mono.just(Message("중복된 값이 존재합니다.-이거 안됨"))
                }
                .onErrorResume(Exception::class.java) { Mono.just(Message("GC지놈 담당자에게 문의 바랍니다.")) }
        }
    }

    fun insertOrganization(userId: String, authority: String?, dto: Organization): Mono<Message> {
        return withAdminAuthority(authority) {
            dslContext.dsl().insertOrganization(userId, dto).map {
                Message.toModelOrg(it, "추가 완료 되었습니다.")
            }
        }
    }


    fun insertUser(userId: String, authority: String?, dto: User): Mono<Message> {
        return withAdminAuthority(authority) {
            Mono.from(dslContext.transactionPublisher { trx ->
                trx.dsl().run {
                    userDao.insertUser(dto).flatMap { userRecord ->
                        insertUserToOrganization(userRecord).map {
                            Message.toModelUser(userRecord, "추가 완료 되었습니다.")
                        }
                    }
                        .onErrorResume(DuplicateKeyException::class.java) {
                            Mono.just(Message("중복된 값이 존재합니다.-이거 안됨"))
                        }
                        .onErrorResume { e ->
                            val errorDetails = e.toString().substringAfterLast("; ").replace("\\\\", "")
                            Mono.just(Message("GC지놈 담당자에게 문의 바랍니다.").apply { error = errorDetails })
                        }
                }

            })
        }
    }

    fun deleteUser(userId: String, authority: String?): Mono<Message> {
        return withAdminAuthority(authority) {
            dslContext.dsl().deleteOrganization(userId)
                .then(userDao.deleteUser(userId)).map{ userRecord ->
                    Message.toModelUser(userRecord, "삭제 완료 되었습니다.")
                }
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
}