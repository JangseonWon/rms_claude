package com.gcgenome.rms.postfile

import com.gcgenome.rms.dao.PostFileDao
import com.gcgenome.rms.data.PostFileDTO
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.util.*

@Component
class PostFileHandler(
    val dslContext: DSLContext,
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}") private val bucketName: String
): PostFileDao {
    fun downloadByFileId(postFileId: UUID): Mono<Pair<String, ByteArray>> {
        return dslContext.selectPostFileById(postFileId)
            .flatMap { file ->
                val getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.path)
                    .build()
                Mono.fromFuture { s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()) }
                    .map { byteArray -> file.name!! to byteArray.asByteArray() }
            }
    }

    fun deletePostFileById(fileId: UUID): Mono<PostFileDTO> {
        return dslContext.selectPostFileById(fileId)
            .flatMap { file ->
                val deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.path)
                    .build()

                Mono.fromFuture { s3Client.deleteObject(deleteObjectRequest) }
                    .then(dslContext.deletePostFileById(fileId))
                    .thenReturn(file)
            }
    }
}