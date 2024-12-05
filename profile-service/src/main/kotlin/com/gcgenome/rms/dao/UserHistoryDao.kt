package com.gcgenome.rms.dao

import com.gcgenome.rms.data.UserHistoryDTO
import com.gcgenome.rms.tables.pojos.UserHistory
import com.gcgenome.rms.tables.references.USER_HISTORY
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface UserHistoryDao {
    fun DSLContext.insertUserHistory(userHistory: UserHistoryDTO): Mono<UserHistory> {
        return Mono.from(
            insertInto(USER_HISTORY)
                .set(USER_HISTORY.USER_ID, userHistory.userId)
                .set(USER_HISTORY.CHANGED_BY, userHistory.changedBy)
                .set(USER_HISTORY.CHANGED_AT, LocalDateTime.now())
                .set(USER_HISTORY.FIELD_NAME, userHistory.fieldName)
                .set(USER_HISTORY.NEW_VALUE, userHistory.newValue)
                .set(USER_HISTORY.OLD_VALUE, userHistory.oldValue)
                .returning()
        ).map { it.into(UserHistory::class.java) }
    }
}