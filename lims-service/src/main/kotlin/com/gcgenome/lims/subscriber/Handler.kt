package com.gcgenome.lims.subscriber


import com.gcgenome.lims.dao.ReportDao
import com.gcgenome.lims.dao.RequestDao
import com.gcgenome.lims.dao.SampleDao
import com.gcgenome.lims.data.*
import com.gcgenome.lims.exception.InvalidWorkflowException
import com.gcgenome.lims.exception.NotFoundBarcodeException
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
    fun updateRequest(message: WorkflowMessage): Mono<Request> {
        val barcode = message.request.samples[0].id.toString()
        val serviceId = message.request.service.id

        return Mono.from(dslContext.run {
            selectSampleByBarcode(barcode)
                .flatMap { sample -> selectRequestBySampleIdAndServiceId(sample.id!!, serviceId) }
                .flatMap { request ->
                    when{
                        message.process == LimsStatus.SPECIFIED.name && message.type == LimsStatus.COMPLETE.name ->
                            updateRequestStatusById(request.serviceId!!, request.sampleId!!, message, Status.IN_PROGRESS)
                        message.process == LimsStatus.FINISHED .name && message.type == LimsStatus.COMPLETE.name ->
                            updateRequestStatusById(request.serviceId!!, request.sampleId!!, message, Status.DELIVERED)
                        message.process == LimsStatus.RESAMPLED.name && message.type == LimsStatus.COMPLETE.name ->
                            updateRequestStatusById(request.serviceId!!, request.sampleId!!, message, Status.TEST_FAILED)
                        else -> Mono.empty()
                    }
                }
        })
    }
    fun saveReport(message: ReportMessage): Mono<Report> {
        val barcode = message.sample.toString()
        val serviceId = message.service!!
        val institution = message.institution
        val reportBytes = message.report ?: byteArrayOf()

        return Mono.from(dslContext.run {
            selectSampleByBarcode(barcode)
                .flatMap { sample -> selectRequestBySampleIdAndServiceId(sample.id!!, serviceId) }
                .flatMap { request ->
                    val year = barcode.substring(0, 4)
                    val month = barcode.substring(4, 6)
                    val day = barcode.substring(6, 8)
                    val now = LocalDateTime.now()
                    val timestamp = now.toInstant(ZoneOffset.UTC).toEpochMilli()

                    updateReportIsLatestBySampleIdAndServiceId(request.sampleId!!, request.serviceId!!)
                        .then(
                            insertReport(Report(
                                id = UUID.randomUUID(),
                                type = "PDF",
                                value = "reports/$institution/$year/$month/$day/$barcode/${barcode}_${serviceId}_${timestamp}.pdf",
                                createAt = now,
                                reportedAt = null,
                                isLatest = true,
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
                    val requestBody = AsyncRequestBody.fromBytes(reportBytes)
                    Mono.fromFuture { s3Client.putObject(putObjectRequest, requestBody) }
                        .then(Mono.just(report))
                }
        })
    }
}