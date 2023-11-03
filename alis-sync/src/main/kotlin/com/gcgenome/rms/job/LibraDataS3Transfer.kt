package com.gcgenome.rms.job

import com.gcgenome.rms.service.ReportDatabaseSync
import com.gcgenome.rms.service.S3Transfer
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelSftp.LsEntry
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.*


@Service
class LibraDataS3Transfer(
    val report: ReportDatabaseSync,
    val s3Transfer: S3Transfer
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
                val remotePath = entry.filename
                if (remotePath !in setOf(".", "..")) {
                    if (remoteType.startsWith("d")) {
                        exploreDirectory("$directory/$remotePath")
                    } else if (!remotePath.endsWith(".pdf") && !remotePath.endsWith(".jpg")) {
                        remoteFileList.add("$directory/$remotePath")
                    }
                }
            }
        }

        exploreDirectory(libraDirectory)

        for (remoteFilePath in remoteFileList) {
            val s3FilePath = s3Transfer.buildLibraS3FilePath(remoteFilePath)
            val inputStream: InputStream = channel.get(remoteFilePath)
            val reportId = UUID.randomUUID()
            val part = s3FilePath.split("/")
            val lastIndex = s3FilePath.lastIndexOf("/")
            val type = part.last().split(".").last()
            val partExceptFileName = s3FilePath.substring(0, lastIndex)
            val filePath = "$partExceptFileName/$reportId.$type"

            logger.info("$reportId reportDatabaseSync start")
            report.reportDatabaseSync(s3FilePath, reportId, filePath).subscribe()
            inputStream.use { s3Transfer.readInputStreamUsingFileChannel(it, filePath) }
        }
        channel.disconnect()
        sshSession.disconnect()
        logger.info("s3 transfer done")
    }
}