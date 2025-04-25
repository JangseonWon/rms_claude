package com.gcgenome.rms.dao

import com.gcgenome.rms.data.SampleDTO
import com.gcgenome.rms.tables.references.REQUEST
import com.gcgenome.rms.tables.references.SAMPLE
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.util.*

interface SampleDao {
    fun DSLContext.deleteSampleById(sampleId: UUID): Mono<SampleDTO> {
        return Mono.from(
            deleteFrom(SAMPLE)
                .where(
                    SAMPLE.ID.eq(sampleId)
                        .andNotExists(
                            selectOne()
                                .from(REQUEST)
                                .where(REQUEST.SAMPLE_ID.eq(sampleId))
                        )
                ).returning()
        ).map { it.into(SampleDTO::class.java) }
    }
}