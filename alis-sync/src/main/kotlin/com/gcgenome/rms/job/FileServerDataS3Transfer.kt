package com.gcgenome.rms.job

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import software.amazon.awssdk.services.s3.S3AsyncClient

class FileServerDataS3Transfer(
    val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}")
    val bucketName: String
) {
    private val logger = LoggerFactory.getLogger("FileServer data sync")

    fun fileServerDataS3Transfer(filePath: String) {
        val jsch = JSch()

        val sshSession: Session = jsch.getSession("administrator", "172.19.208.203", 14992)
        sshSession.setPassword("pmc1004!")
        sshSession.setConfig("StrictHostKeyChecking", "no")
        sshSession.connect()

        val channel: ChannelSftp = sshSession.openChannel("sftp") as ChannelSftp
        channel.connect()

        val fileServerDirectory = "D:\\파일서버\\LocalUser\\LC32_GCGENOME"

    }
}