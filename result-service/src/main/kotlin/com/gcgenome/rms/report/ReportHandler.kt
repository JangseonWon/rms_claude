package com.gcgenome.rms.report

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.ReportDao
import com.gcgenome.rms.dao.RequestDao
import com.gcgenome.rms.data.RequestDTO
import com.gcgenome.rms.data.Role
import com.gcgenome.rms.exception.AuthenticationNotFoundException
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Component
class ReportHandler(
    val dslContext: DSLContext,
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}") private val bucketName: String
):ReportDao, RequestDao {
    fun downloadFiles(userAuth: UserAuthentication, requests: List<RequestDTO>): Mono<ByteArray> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                Flux.fromIterable(requests)
                    .flatMap { request ->
                        val reports = request.reports ?: emptyList()
                        Flux.fromIterable(reports)
                            .flatMap { report ->
                                val isAuthorizedUser = userAuth.user.id == request.order?.user?.id
                                val isAdminOrManager = userAuth.user.role in listOf(Role.ADMIN.toString(), Role.MANAGER.toString())

                                if (isAuthorizedUser) {
                                    updateRequestStatusFinish(request)
                                        .then(updateReportReportedAt(report.id!!))
                                        .then(downloadFileFromS3(report.value!!))
                                        .map { fileContent -> report.value!!.substringAfterLast("/") to fileContent }
                                } else if (isAdminOrManager) {
                                    downloadFileFromS3(report.value!!)
                                        .map { fileContent -> report.value!!.substringAfterLast("/") to fileContent }
                                }else {
                                    Mono.error<Pair<UUID, ByteArray>>(AuthenticationNotFoundException())
                                }
                            }
                    }
                    .collectList()
                    .map { files ->
                        ByteArrayOutputStream().use { baos ->
                            ZipOutputStream(baos).use { zipOut ->
                                files.forEach { (fileName, fileContent) ->
                                    val zipEntry = ZipEntry("$fileName")
                                    zipOut.putNextEntry(zipEntry)
                                    zipOut.write(fileContent)
                                    zipOut.closeEntry()
                                }
                            }
                            baos.toByteArray()
                        }
                    }

            }
        })

    }
    fun downloadFile(reportId: UUID, userAuth: UserAuthentication): Mono<ByteArray> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run { selectReportById(reportId)
                .flatMap { report ->
                    val isAuthorizedUser = userAuth.user.id == report.request?.order?.user?.id
                    val isAdminOrManager = userAuth.user.role in listOf(Role.ADMIN.toString(), Role.MANAGER.toString())

                    if(isAuthorizedUser){
                        updateRequestStatusFinish(report.request!!)
                            .then(updateReportReportedAt(report.id!!))
                            .then(downloadFileFromS3(report.value!!))
                    } else if(isAdminOrManager) {
                        downloadFileFromS3(report.value!!)
                    } else{
                        Mono.error(AuthenticationNotFoundException())
                    }
                }
            }
        })
    }
    private fun downloadFileFromS3(key: String): Mono<ByteArray> {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        return Mono.fromFuture {
            s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
        }.map { it.asByteArray() }
    }
}