package com.gcgenome.rms.dao

import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import reactor.core.publisher.Mono


interface SampleDao {
    fun DSLContext.checkOrganizationIdBySample(userId: String, organizationId: String): Mono<Int> {
        return Mono.from(
            select(count(SAMPLE.ORGANIZATION_ID))
                .from(SAMPLE)
                .where(SAMPLE.ORGANIZATION_ID.eq(organizationId).and(SAMPLE.USER_ID.eq(userId)))
        ).map { it.component1() }
    }
}