package com.gcgenome.lims.subscriber


import com.gcgenome.lims.dao.ReportDao
import com.gcgenome.lims.dao.RequestDao
import com.gcgenome.lims.dao.SampleDao
import com.gcgenome.lims.data.Message
import com.gcgenome.lims.data.Report
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class Handler(
    val dslContext: DSLContext,
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}") private val bucketName: String
) : SampleDao, ReportDao, RequestDao {
    fun saveReport(message: Message): Mono<Report> {
        return Mono.from(dslContext.run {
            selectSampleByBarcode(message.sample.toString(), message.institution!!)
                .switchIfEmpty(Mono.empty())
                .flatMap { sample -> selectRequestBySampleIdAndServiceId(sample.id!!, message.service!!) }
                .switchIfEmpty(Mono.empty())
                .flatMap { request -> updateRequestStatusById(request.orderId!!, request.serviceId!!, request.sampleId!!) }
                .flatMap { request ->
                    val barcode = message.sample.toString()
                    val year = barcode.substring(0, 4)
                    val month = barcode.substring(4, 6)
                    val day = barcode.substring(6, 8)
                    val now = LocalDateTime.now()
                    val timestamp = now.toInstant(ZoneOffset.UTC).toEpochMilli()

                    updateReportIsLatestBySampleIdAndServiceId(request.orderId!!, request.sampleId!!, request.serviceId!!)
                        .then(
                            insertReport(Report(
                                id = UUID.randomUUID(),
                                type = "PDF",
                                value = "reports/${message.institution}/${year}/${month}/${day}/${barcode}/${barcode}_${message.service}_${timestamp}.pdf",
                                createAt = now,
                                reportedAt = null,
                                isLatest = true,
                                orderId = request.orderId,
                                serviceId = request.serviceId,
                                sampleId = request.sampleId)
                            )
                        )
                }
                .flatMap { report ->
                    val putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(report.value)
                        .contentType("application/pdf")
                        .build()
                    val requestBody = AsyncRequestBody.fromBytes(message.report ?: byteArrayOf())
                    Mono.fromFuture { s3Client.putObject(putObjectRequest, requestBody) }
                        .then(Mono.just(report))
                }
        })
    }
}