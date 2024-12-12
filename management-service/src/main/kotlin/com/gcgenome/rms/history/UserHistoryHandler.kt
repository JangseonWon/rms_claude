package com.gcgenome.rms.history

import com.gcgenome.rms.dao.UserDao
import com.gcgenome.rms.dao.UserHistoryDao
import com.gcgenome.rms.data.UserDTO
import com.gcgenome.rms.data.UserHistoryDTO
import com.gcgenome.rms.tables.pojos.UserHistory
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class UserHistoryHandler(
    val dslContext: DSLContext,
) : UserDao, UserHistoryDao {
    fun logUserChanges(dsl: DSLContext, hostUserId: String, updateUser: UserDTO): Mono<UserHistory> {
        val userId = updateUser.id

        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectUserById(userId)
                    .flatMap { oldUser ->
                        userUpdateLog(dsl(), hostUserId, updateUser, oldUser)
                    }
                    .switchIfEmpty(
                        userInsertLog(dsl, hostUserId, updateUser)
                    )
            }
        })
    }

    private fun userUpdateLog(dsl: DSLContext, hostUserId: String, updateUser: UserDTO, oldUser: UserDTO): Mono<UserHistory> {
        val userId = updateUser.id
        val fieldsToCompare = listOf(
            "name" to Pair(oldUser.name, updateUser.name),
            "password" to Pair(oldUser.password, updateUser.password),
            "phone_number" to Pair(oldUser.phoneNumber, updateUser.phoneNumber),
            "branch_name" to Pair(oldUser.branchName, updateUser.branchName),
            "branch_serial" to Pair(oldUser.branchSerial, updateUser.branchSerial),
            "email" to Pair(oldUser.email, updateUser.email),
            "state" to Pair(oldUser.state, updateUser.state)
        )

        val userHistoryEntries = fieldsToCompare.mapNotNull { (fieldName, values) ->
            val (oldValue, newValue) = values
            if (newValue != null && oldValue != newValue) {
                UserHistoryDTO(
                    userId = userId,
                    changedBy = hostUserId,
                    fieldName = fieldName,
                    oldValue = oldValue,
                    newValue = newValue
                )
            } else null
        }

        return Flux.fromIterable(userHistoryEntries)
            .flatMap { dsl.insertUserHistory(it) }
            .collectList()
            .mapNotNull { it.lastOrNull() }
    }

    fun userInsertLog(dsl: DSLContext, hostUserId: String, updateUser: UserDTO): Mono<UserHistory> {
        val userId = updateUser.id
        val userHistoryEntries = listOf(
            "name" to updateUser.name,
            "password" to updateUser.password,
            "phone_number" to updateUser.phoneNumber,
            "branch_name" to updateUser.branchName,
            "branch_serial" to updateUser.branchSerial,
            "email" to updateUser.email,
            "state" to updateUser.state
        ).mapNotNull { (fieldName, newValue) ->
            if (newValue != null) {
                UserHistoryDTO(
                    userId = userId,
                    changedBy = hostUserId,
                    fieldName = fieldName,
                    oldValue = null,
                    newValue = newValue
                )
            } else null
        }

        return Flux.fromIterable(userHistoryEntries)
            .flatMap { dsl.insertUserHistory(it) }
            .collectList()
            .mapNotNull { it.lastOrNull() }
    }
}


//class UserHistoryHandler(
//    val dslContext: DSLContext,
//): UserDao, UserHistoryDao {
//    fun userUpdateLog(hostUserId: String?, updateUser: UserDTO): Mono<UserHistory> {
//        return Mono.from(dslContext.transactionPublisher { trx ->
//            val userId = updateUser.id
//            trx.dsl().run {
//                selectUserById(userId).flatMap { oldUser ->
//                    val fieldsToCompare = listOf(
//                        "name" to Pair(oldUser.name, updateUser.name),
//                        "password" to Pair(oldUser.password, updateUser.password),
//                        "phone_number" to Pair(oldUser.phoneNumber, updateUser.phoneNumber),
//                        "branch_name" to Pair(oldUser.branchName, updateUser.branchName),
//                        "branch_serial" to Pair(oldUser.branchSerial, updateUser.branchSerial),
//                        "email" to Pair(oldUser.email, updateUser.email),
//                        "state" to Pair(oldUser.state, updateUser.state)
//                    )
//
//                    val userHistoryEntries = fieldsToCompare.mapNotNull { (fieldName, values) ->
//                        val (oldValue, newValue) = values
//                        if (newValue != null && oldValue != newValue) {
//                            UserHistoryDTO(
//                                userId = userId,
//                                changedBy = hostUserId ?: "Alis Sync",
//                                fieldName = fieldName,
//                                oldValue = oldValue,
//                                newValue = newValue
//                            )
//                        } else null
//                    }
//
//                    Flux.fromIterable(userHistoryEntries)
//                        .flatMap { insertUserHistory(it) }
//                        .collectList()
//                        .mapNotNull { it.lastOrNull() }
//                }
//            }
//        })
//    }
//
//    fun userInsertLog(dsl: DSLContext, hostUserId: String?, updateUser: UserDTO): Mono<UserHistory> {
//        val userId = updateUser.id
//        val userHistoryEntries = listOf(
//            "name" to updateUser.name,
//            "password" to updateUser.password,
//            "phone_number" to updateUser.phoneNumber,
//            "branch_name" to updateUser.branchName,
//            "branch_serial" to updateUser.branchSerial,
//            "email" to updateUser.email,
//            "state" to updateUser.state
//        ).mapNotNull { (fieldName, newValue) ->
//            if (newValue != null) {
//                UserHistoryDTO(
//                    userId = userId,
//                    changedBy = hostUserId ?: "Alis Sync",
//                    fieldName = fieldName,
//                    oldValue = null,
//                    newValue = newValue
//                )
//            } else null
//        }
//        return Flux.fromIterable(userHistoryEntries)
//            .flatMap { dsl.insertUserHistory(it) }
//            .collectList()
//            .mapNotNull { it.lastOrNull() }
//    }
//}