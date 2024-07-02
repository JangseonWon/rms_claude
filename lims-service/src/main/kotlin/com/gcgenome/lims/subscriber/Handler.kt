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
        return Mono.from(dslContext.run {
            if(message.process  == "SPECIFIED" && message.type == "COMPLETE"){
                selectSampleByBarcode(message.request.samples[0].id.toString())
                    .switchIfEmpty(Mono.error(NotFoundBarcodeException("not found barcode: ${message.request.samples[0].id}")))
                    .flatMap { sample -> selectRequestBySampleIdAndServiceId(sample.id!!, message.request.service.id) }
                    .flatMap { request -> updateRequestStatusById(request.orderId!!, request.serviceId!!, request.sampleId!!, "INPROGRESS") }
            }else{
                Mono.error(InvalidWorkflowException("It is not in specified & complete - barcode: ${message.request.samples[0].id}, service: ${message.request.service.id}, status: ${message.process}, type: ${message.type}"))
            }
        })
    }
    fun saveReport(message: ReportMessage): Mono<Report> {
        return Mono.from(dslContext.run {
            selectSampleByBarcode(message.sample.toString())
                .switchIfEmpty(Mono.error(NotFoundBarcodeException("not found barcode: ${message.sample}")))
                .flatMap { sample -> selectRequestBySampleIdAndServiceId(sample.id!!, message.service!!) }
                .flatMap { request -> updateRequestStatusById(request.orderId!!, request.serviceId!!, request.sampleId!!, "DELIVERED") }
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