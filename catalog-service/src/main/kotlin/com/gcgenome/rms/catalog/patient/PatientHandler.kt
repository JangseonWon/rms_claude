package com.gcgenome.rms.catalog.patient

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.PatientDao
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.PatientDTO
import com.gcgenome.rms.data.Query
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PatientHandler(val dslContext: DSLContext): PatientDao {
    fun selectPatients(user: UserAuthentication, query: Query):  Mono<Page<PatientDTO>> {
        return dslContext.selectPatientsWithPage(query, user.user)
    }
}