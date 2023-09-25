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
    @Value("\${cloud.aws.s3.bucket}")
    val bucketName: String
): ReportDao, SampleDao {
    fun findDownloadPath(reportId: UUID): Mono<String> {
        return dslContext.dsl().selectReportById(reportId, ReportType.PDF)
            .switchIfEmpty(Mono.error(ReportNotFoundException(reportId)))
            .mapNotNull { it.path }
    }

    fun downloadFile(path: String): Mono<ResponseBytes<GetObjectResponse>> {
        val getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(path).build()
        return Mono.fromFuture { s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()) }
    }

    fun savePdf(): Mono<CompletableFuture<PutObjectResponse>> {
        val file = CompletableFuture.supplyAsync {
            s3Client.putObject(
                PutObjectRequest.builder().bucket(bucketName).key("hello.txt").build(),
                AsyncRequestBody.fromBytes("hello".toByteArray())
            )
        }
        return Mono.fromFuture {file}
    }
    fun findReports(userId: String, orderDateFrom: LocalDateTime, orderDateTo: LocalDateTime): Flux<Sample> {
        return dslContext.dsl().selectSampleByCreateAt(userId, orderDateFrom, orderDateTo)
    }

    fun downloadReport(userId: String, reportId: UUID): Mono<ResponseBytes<GetObjectResponse>> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                selectReportById(reportId, ReportType.PDF)
                    .switchIfEmpty(Mono.error(ReportNotFoundException(reportId)))
                    .flatMap { report -> report.completeAt?.let { Mono.error(CompletedReportException()) } ?: Mono.just(report) }
                    .then(updateReportCompete(reportId))
                    .then(findDownloadPath(reportId))
                    .flatMap { path -> downloadFile(path)}
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