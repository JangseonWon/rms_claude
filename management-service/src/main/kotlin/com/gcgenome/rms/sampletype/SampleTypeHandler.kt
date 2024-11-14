package com.gcgenome.rms.sampletype

import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.data.SampleTypeDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SampleTypeHandler(
    val dslContext: DSLContext
):SampleTypeDao {
    fun selectSampleType(sampleTypeId: String): Mono<SampleTypeDTO> {
        return dslContext.selectSampleTypeById(sampleTypeId)
    }
    fun selectSampleTypes(query: Query): Mono<Page<SampleTypeDTO>> {
        return dslContext.selectSampleTypesWithPage(query)
    }
    fun updateSampleType(sampleType: SampleTypeDTO): Mono<Void> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                updateSampleTypeById(sampleType).then()
            }
        })
    }
}