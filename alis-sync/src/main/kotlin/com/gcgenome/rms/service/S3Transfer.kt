package com.gcgenome.rms.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

@Service
class S3Transfer(
    val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}")
    val bucketName: String
) {
    private val logger = LoggerFactory.getLogger("S3 Transfer")

    fun readInputStreamUsingFileChannel(inputStream: InputStream, s3FilePath: String): ByteArray {
        val tempFile = File.createTempFile("tempFile", null)
        val channel = FileChannel.open(tempFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)

        val buffer = ByteBuffer.allocate(1024)
        var len: Int
        try {
            while (inputStream.read(buffer.array()).also { len = it } != -1) {
                buffer.clear()
                buffer.limit(len)
                channel.write(buffer)
            }
        } finally {
            channel.close()
            inputStream.close()
        }

        val dataFile = File.createTempFile("dataFile", null)
        val dataChannel = FileChannel.open(dataFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)

        val tempFileChannel = FileChannel.open(tempFile.toPath(), StandardOpenOption.READ)
        tempFileChannel.transferTo(0, tempFileChannel.size(), dataChannel)
        tempFileChannel.close()
        dataChannel.close()

        s3Save(s3FilePath, dataFile)

        tempFile.delete()

        return ByteArray(0)
    }

    fun s3Save(s3FilePath: String, saveFile: File) {
        val putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(s3FilePath).build()
        s3Client.putObject(putObjectRequest, AsyncRequestBody.fromFile(saveFile))
        logger.info("$s3FilePath save")
    }

    fun buildLibraS3FilePath(remoteFilePath: String): String {
        val fullPath = remoteFilePath.substring(remoteFilePath.lastIndexOf("/", 1) + 1)
        val s3Directory = "reports"
        val parts = fullPath.split("/")
        val directory = parts[parts.size - 2]
        val date = directory.substring(0, 8)
        val year = date.substring(0, 4)
        val month = date.substring(4, 6)
        val day = date.substring(6, 8)
        val branchCode = directory.substring(8, 11)
        val orderNumber = directory.substring(11, 15)
        val serviceCode = directory.substring(15)
        val fileName = parts.last()

        return "$s3Directory/$year/$month/$day/$branchCode/$orderNumber/$serviceCode/$fileName"
    }
}