package com.gcgenome.rms.report

import com.gcgenome.rms.data.ReportType
import com.gcgenome.rms.dao.ReportDao
import com.gcgenome.rms.dao.SampleDao
import com.gcgenome.rms.data.Report
import com.gcgenome.rms.data.Sample
import com.gcgenome.rms.data.SampleState
import com.gcgenome.rms.exceptions.CompletedReportException
import com.gcgenome.rms.exceptions.ReportNotFoundException
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class Handler(
    val dslContext: DSLContext,
    val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}")
    val bucketName: String
): ReportDao, SampleDao {
    fun downloadFile(path: String): Mono<ResponseBytes<GetObjectResponse>> {
        val getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(path).build()
        return Mono.fromFuture { s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()) }
    }
    fun findReports(userId: String, orderDateFrom: LocalDateTime, orderDateTo: LocalDateTime): Flux<Sample> {
        return dslContext.selectSampleByCreateAt(userId, orderDateFrom, orderDateTo)
    }

    fun downloadReport(userId: String, reportId: UUID): Mono<ResponseBytes<GetObjectResponse>> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectReportById(reportId)
                    .switchIfEmpty(Mono.error(ReportNotFoundException(reportId)))
                    .flatMap { report -> report.reportedAt?.let { Mono.error(CompletedReportException()) } ?: Mono.just(report) }
                    .then(updateReportReportedAt(reportId))
                    .flatMap { report ->
                        when (report.type) {
                            ReportType.PDF -> updateSampleState(report.sampleId!!, SampleState.REPORTED).then(downloadFile(report.path!!))
                            else -> downloadFile(report.path!!)
                        }
                    }
            }
        })
    }

    fun saveReport(sampleId: UUID, reportDto: Report): Mono<Report> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                updateSampleState(sampleId, SampleState.FINISHED)
                    .then(saveReport(sampleId, reportDto))
            }
        })
    }
}