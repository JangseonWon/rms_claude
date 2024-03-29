package com.gcgenome.rms.download

import com.gcgenome.rms.dao.PatientDao
import com.gcgenome.rms.dao.SampleExtensionDao
import com.gcgenome.rms.tables.pojos.Patient
import com.gcgenome.rms.tables.pojos.SampleExtension
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID


@Service
class DownloadHandler(
    private val dslContext: DSLContext,
    private val niptHandler: NiptHandler,
    private val genomeHealthHandler: GenomeHealthHandler
): SampleExtensionDao, PatientDao {

    fun selectSampleExtensions(sampleId : UUID) : Mono<List<SampleExtension>>{
        return dslContext.selectSampleExtensions(sampleId).collectList()
    }

    fun selectPatient(sampleId : UUID) : Mono<Patient> {
        return dslContext.selectPatient(sampleId)
    }

    fun handleDownloadByService(principal: String, serviceId : String, sampleId: UUID): Mono<ByteArray> {
        return Mono.from( selectPatient(sampleId).flatMap { patient ->
            selectSampleExtensions(sampleId).flatMap { extensions ->
                when (serviceId) {
                    "O001" -> niptHandler.niptDownload(principal, extensions, patient)
                    else -> Mono.error(IllegalArgumentException("Unsupported service: $serviceId"))
                }
            }
        })
    }
}