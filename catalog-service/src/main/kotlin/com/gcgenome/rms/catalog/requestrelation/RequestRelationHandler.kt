package com.gcgenome.rms.catalog.requestrelation

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.RequestRelationDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class RequestRelationHandler(val dslContext: DSLContext):
    RequestRelationDao
{
    fun getRequestGroups(): Flux<RequestRelationDTO> {
        return dslContext.selectRequestRelations()
    }
}