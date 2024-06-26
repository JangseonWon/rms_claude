package com.gcgenome.rms.download

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.PatientDao
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.dao.SampleDao
import com.gcgenome.rms.dao.SampleExtensionDao
import com.gcgenome.rms.data.Patient
import com.gcgenome.rms.tables.pojos.SampleExtension
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.util.*


@Service
class DownloadHandler(
    private val dslContext: DSLContext,
    private val niptHandler: NiptHandler,
    private val genomeHealthHandler: GenomeHealthHandler,
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}") private val bucketName: String
): RequestDao, SampleDao, SampleExtensionDao, PatientDao {

    fun selectSampleExtensions(sampleId : UUID) : Mono<List<SampleExtension>>{
        return dslContext.selectSampleExtensions(sampleId).collectList()
    }

    fun selectPatient(sampleId : UUID) : Mono<Patient> {
        return dslContext.selectPatientBySampleId(sampleId)
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

    fun downloadByRequestId(authentication: UserAuthentication, requestId: String): Mono<ByteArray> {
        val parts = requestId.split("_")
        val barcode = parts[0]
        val serviceId = parts[1]
        val year = requestId.substring(0, 4)
        val month = requestId.substring(4, 6)
        val day = requestId.substring(6, 8)
        val userIdMono = dslContext.selectSampleUserIdByBarcode(barcode)

        return userIdMono.flatMap { userId ->
            val s3Key = "reports/$userId/$year/$month/$day/$requestId.pdf"
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build()

            val downloadMono = Mono.fromFuture {
                s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
            }.map { it.asByteArray() }

            val updateStatus = dslContext.changeStatusToFinished(serviceId, barcode)

            downloadMono.flatMap { byteArray ->
                updateStatus.thenReturn(byteArray)
            }
        }
    }

    fun downloadByServiceSampleFile(serviceName: String): Mono<ByteArray> {
        val s3Key = "services/$serviceName/${serviceName}_form.xlsx"
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build()

        return Mono.fromFuture {
            s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
        }.map { it.asByteArray() }
    }
}