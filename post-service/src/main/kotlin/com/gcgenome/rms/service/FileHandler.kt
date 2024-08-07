package com.gcgenome.rms.service

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.FileDao
import com.gcgenome.rms.tables.pojos.PostFile
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class FileHandler(
    val dslContext: DSLContext,
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}") private val bucketName: String
): FileDao {
    fun downloadByFileId(postId: UUID, fileId: UUID): Mono<Pair<String, ByteArray>> {
        return getFileByPostId(postId, fileId)
            .flatMap { file ->
                val getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.path)
                    .build()
                Mono.fromFuture { s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()) }
                    .map { byteArray -> file.name!! to byteArray.asByteArray() }
            }
    }

    fun uploadFiles(parts: MultiValueMap<String, Part>, authentication: UserAuthentication, postId: UUID): Mono<List<PostFile>> {
        val fileParts = parts["file"]?.filterIsInstance<FilePart>() ?: emptyList()
        return if (fileParts.isNotEmpty()) {
            Flux.fromIterable(fileParts)
                .flatMap { filePart -> uploadFile(authentication.user.id!!, postId, filePart) }
                .collectList()
        } else {
            Mono.error(IllegalArgumentException("No files uploaded"))
        }
    }

    fun uploadFile(userId: String, postId: UUID, filePart: FilePart): Mono<PostFile> {
        val fileName = filePart.filename()
        val currentDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val formattedDate = currentDate.format(dateFormatter)
        val fileId = UUID.randomUUID()
        val filePath = "post/$userId/$formattedDate/$postId/$fileName"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build()

        return filePart.content()
            .collectList()
            .flatMap { dataBufferList ->
                val byteBuffer = ByteBuffer.allocate(dataBufferList.sumOf { it.readableByteCount() })
                dataBufferList.forEach { byteBuffer.put(it.asByteBuffer()) }
                byteBuffer.flip()

                Mono.fromFuture { s3Client.putObject(putObjectRequest, AsyncRequestBody.fromByteBuffer(byteBuffer)) }
            }
            .flatMap {
                val postFile = PostFile(
                    id = fileId,
                    postId = postId,
                    name = fileName,
                    path = filePath,
                )
                Mono.from(dslContext.insertFile(postFile))
            }
    }

    fun deleteFileById(postId: UUID, fileId: UUID): Mono<PostFile> {
        return getFileByPostId(postId, fileId)
            .flatMap { file ->
                val deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.path)
                    .build()

                Mono.fromFuture { s3Client.deleteObject(deleteObjectRequest) }
                    .then(Mono.from(dslContext.deleteFileById(fileId)))
                    .thenReturn(file)
            }
    }

    fun deleteDirectoryByPostId(postId: UUID): Mono<PostFile> {
        return getFileAllByPostId(postId)
            .flatMap { file ->
                val path = file.path
                val directory = path?.substring(0, path.lastIndexOf('/'))

                val listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(directory + "/")
                    .build()
                Mono.fromFuture { s3Client.listObjectsV2(listObjectsRequest) }
                    .flatMap { listObjectsResponse ->
                        val keysToDelete = listObjectsResponse.contents().map { it.key() }

                        if (keysToDelete.isEmpty()) {
                            Mono.empty()
                        } else {
                            val deleteObjectsRequest = DeleteObjectsRequest.builder()
                                .bucket(bucketName)
                                .delete {
                                    it.objects(keysToDelete.map { key -> ObjectIdentifier.builder().key(key).build() })
                                }
                                .build()
                            Mono.fromFuture { s3Client.deleteObjects(deleteObjectsRequest) }
                                .then(Mono.from(dslContext.deleteFileByPostId(postId)))
                                .thenReturn(file)
                        }
                    }
            }
    }

    fun getFileByPostId(postId: UUID, fileId: UUID): Mono<PostFile> {
        return Mono.from(dslContext.getFileById(postId, fileId))
    }

    fun getFileAllByPostId(postId: UUID): Mono<PostFile> {
        return Mono.from(dslContext.getFileByPostId(postId))
    }
}