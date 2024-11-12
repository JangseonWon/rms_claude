package com.gcgenome.rms.post

import com.gcgenome.rms.authentication.UserAuthentication
import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.*
import com.gcgenome.rms.tables.pojos.Post
import com.gcgenome.rms.tables.pojos.PostRead
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.nio.ByteBuffer
import java.time.format.DateTimeFormatter

@Component
class PostHandler(
    val dslContext: DSLContext,
    val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}") private val bucketName: String
): PostDao, PostFileDao, CommentDao, UserDao, PostReadDao {
    private val webClient: WebClient = WebClient.builder()
        .baseUrl("https://wh.jandi.com/connect-api/webhook")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.tosslab.jandi-v2+json")
        .build()


    fun selectPosts(authentication: UserAuthentication, query: Query, category: String):  Mono<Page<PostDTO>> {
        val userId = authentication.user.id!!
        authentication.takeIf { it.user.role == Role.USER.toString() && category == "qna" }?.let {
            query.filterGroups = query.filterGroups ?: mutableListOf()  // null 체크 및 초기화
            query.filterGroups?.add(
                Query.FilterGroup(
                    filters = listOf(
                        Query.FilterGroup.Filter(
                            table = "post",
                            column = "user_id",
                            operator = "=",
                            value = userId
                        )
                    )
                )
            )
        }
        return dslContext.selectPostsWithPage(query, category, userId)
    }

    fun selectPost(postId: Long, user: UserAuthentication): Mono<PostDTO> {
        return Mono.from(dslContext.selectPostById(postId))
    }

    fun insertPost(post: PostDTO, fileParts: List<FilePart>?): Mono<PostDTO> {
        return dslContext.insertPost(post)
            .flatMap { savedPost ->
                val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                val formattedDate = savedPost.createAt!!.format(formatter)
                val filePath = "post/${post.user!!.id}/${formattedDate}/${savedPost.id}"

                insertPostRead(post.user?.id!!, savedPost).then(
                    fileParts?.let {
                        uploadFilesToS3(savedPost.id!!, it, filePath)
                    } ?: Mono.empty()
                ).thenReturn(savedPost)
            }
    }

    fun insertPostRead(userId: String, post: PostDTO): Flux<PostRead> {
        return Flux.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                userPermissionsSelectUser(userId)
                    .flatMap({ insertPostRead(post.id!!, it.id!!) }, 10)
            }
        })
    }

    fun updatePost(post: PostDTO, fileParts: List<FilePart>?): Mono<PostDTO> {
        return dslContext.updatePost(post)
            .flatMap { updatePost ->
                val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                val formattedDate = updatePost.createAt!!.format(formatter)
                val filePath = "post/${post.user!!.id}/${formattedDate}/${updatePost.id}"

                val uploadTasks = fileParts?.let {
                    uploadFilesToS3(updatePost.id!!, it, filePath)
                } ?: Mono.empty()
                uploadTasks.thenReturn(updatePost)
            }
    }
    private fun uploadFilesToS3(postId: Long, fileParts: List<FilePart>, filePath: String): Mono<Void> {
        return Flux.fromIterable(fileParts)
            .flatMap { filePart ->
                val path = "$filePath/${filePart.filename()}"
                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(path)
                    .build()

                filePart.content()
                    .collectList()
                    .map { dataBuffers ->
                        val totalBuffer = ByteBuffer.allocate(dataBuffers.sumOf { it.readableByteCount() })
                        dataBuffers.forEach { buffer -> totalBuffer.put(buffer.asByteBuffer()) }
                        totalBuffer.flip()
                        totalBuffer
                    }
                    .flatMap { byteBuffer ->
                        Mono.fromFuture {
                            s3Client.putObject(putObjectRequest, AsyncRequestBody.fromByteBuffer(byteBuffer))
                        }.then(
                            dslContext.insertPostFile(PostFileDTO(path = path, name = filePart.filename(), post = PostDTO(id = postId)))
                        )
                    }
            }.then()
    }

    fun deletePost(postId: Long): Mono<Post> {
        return Mono.from(dslContext.transactionPublisher { trx ->
            trx.dsl().run {
                deletePostFileByPostId(postId)
                    .flatMap { postFile ->
                        val deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(postFile.path)
                            .build()
                        Mono.fromFuture { s3Client.deleteObject(deleteObjectRequest) }
                    }.then(deleteCommentByPostId(postId))
                    .then(deletePostByPostId(postId))
            }
        })
    }

    fun postReadByUser(postId: Long, userId: String): Mono<PostRead> {
        return Mono.from(dslContext.updatePostRead(postId, userId))
    }

    fun sendToJandi(postId: Long, category: String, jandiRequest: JandiRequest): Mono<Post> {
        val (connectColor, postCategory, comment) = when (category.lowercase()) {
            "bug" -> Triple("#cd4855", "새로운 질문이 등록 되었습니다.", "")
            "result" -> Triple("#007bff", "새로운 질문이 등록 되었습니다.", "")
            "service" -> Triple("#28a745", "새로운 질문이 등록 되었습니다.", "")
            "others" -> Triple("#a75928", "새로운 질문이 등록 되었습니다.", "")
            "update" -> Triple("#800080", "질문이 수정 되었습니다.", "")
            "comment" -> Triple("#FFFA99", "새로운 댓글이 등록 되었습니다.", "\n댓글: ${jandiRequest.comment?.content}")
            else -> Triple("#000000", "새로운 질문이 등록 되었습니다.", "")
        }

        val requestBody = mapOf(
            "body" to "[$postCategory](https://rms-test.gcgenome.com/qna/${jandiRequest.post.userId}/${postId})",
            "connectColor" to connectColor,
            "connectInfo" to listOf(
                mapOf("title" to "세부내용", "description" to "제목: ${jandiRequest.post.title}\n작성자: ${jandiRequest.userName}$comment")
            )
        )

        return webClient.post()
            .uri("/17558388/81bc697c503328e485da5e22dddaaa4e")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Post::class.java)
    }
}