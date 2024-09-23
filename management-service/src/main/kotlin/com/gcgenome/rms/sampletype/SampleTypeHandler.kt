package com.gcgenome.rms.sampletype

import com.gcgenome.rms.dao.SampleTypeDao
import com.gcgenome.rms.dao.ServiceDao
import com.gcgenome.rms.dao.ServiceExtensionDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.tables.pojos.SampleType
import com.gcgenome.rms.tables.pojos.ServiceSampleType
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class SampleTypeHandler(
    val dslContext: DSLContext
):SampleTypeDao {
    fun selectSampleTypes(query: Query): Mono<Page<SampleType>> {
        return dslContext.selectSampleTypesWithPage(query)
    }
}