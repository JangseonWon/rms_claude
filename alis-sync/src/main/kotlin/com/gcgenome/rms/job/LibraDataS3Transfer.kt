package com.gcgenome.rms.job

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelSftp.LsEntry
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.util.*


@Service
class LibraDataS3Transfer(
    val report: ReportDatabaseSync,
    val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}")
    val bucketName: String
) {
    private val logger = LoggerFactory.getLogger("libra data sync")

    fun libraDataTransfer() {
        val jsch = JSch()

        val sshSession: Session = jsch.getSession("root", "172.19.208.111", 22)
        sshSession.setPassword("snubi1004")
        sshSession.setConfig("StrictHostKeyChecking", "no")
        sshSession.connect()

        val channel: ChannelSftp = sshSession.openChannel("sftp") as ChannelSftp
        channel.connect()

        val libraDirectory = "/data/lis/20230102"

        val remoteFileList = mutableListOf<String>()

        fun exploreDirectory(directory: String) {
            val remoteEntries = channel.ls(directory) as Vector<LsEntry>
            for (entry in remoteEntries) {
                val remoteType = entry.toString().split(" ")[0]
                val isDirectory = remoteType.startsWith("d")
                val remotePath = entry.filename
                if ("." == remotePath || ".." == remotePath) continue
                else {
                    if (isDirectory) {
                        exploreDirectory("$directory/$remotePath")
                    } else {
                        remoteFileList.add("$directory/$remotePath")
                    }
                }
            }
        }

        exploreDirectory(libraDirectory)

        for (remoteFilePath in remoteFileList) {
            val s3FilePath = buildS3FilePath(remoteFilePath)
            val inputStream: InputStream = channel.get(remoteFilePath)
            val reportId = UUID.randomUUID()
            val part = s3FilePath.split("/")
            val lastIndex = s3FilePath.lastIndexOf("/")
            val type = part.last().split(".").last()
            val partExceptFileName = s3FilePath.substring(0, lastIndex)
            val filePath = "$partExceptFileName/$reportId.$type"

            logger.info("$reportId reportDatabaseSync start")
            report.reportDatabaseSync(s3FilePath, reportId, filePath).subscribe()
            inputStream.use { readInputStreamUsingFileChannel(it, filePath) }
        }
        channel.disconnect()
        sshSession.disconnect()
        logger.info("s3 transfer done")
    }

    private fun buildS3FilePath(remoteFilePath: String): String {
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
}