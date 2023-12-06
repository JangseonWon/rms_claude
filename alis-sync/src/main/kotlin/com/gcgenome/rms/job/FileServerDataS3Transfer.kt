package com.gcgenome.rms.job

import com.gcgenome.rms.service.ConvertSlashes
import com.gcgenome.rms.service.S3Transfer
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

@Service
class FileServerDataS3Transfer (
    private val s3Transfer: S3Transfer,
    private val convertSlashes: ConvertSlashes
) {
    private val logger = LoggerFactory.getLogger("FileServer data sync")

    fun findFile() {

    }

    fun fileServerDataS3Transfer(name: String, type: String, windowPath: String, path:String, text: String) {
        val jsch = JSch()

        val sshSession: Session = jsch.getSession("lims", "172.19.216.11", 2222)
        sshSession.setPassword("Gccorp00!")
        sshSession.setConfig("StrictHostKeyChecking", "no")
        sshSession.connect()

        val channel: ChannelSftp = sshSession.openChannel("sftp") as ChannelSftp
        channel.connect()

        val windowFilePath = convertSlashes.convertBackSlashesToSlashes(windowPath)

        val fileServerDirectory = "/Server_Backup/[172.19.208.203]A-LIS_WEB/NasSmbFileBackup_D_drive/D\$/파일서버/LocalUser/LC32_GCGENOME"
        val remoteFilePath = "$fileServerDirectory$windowFilePath$name"

        if (type == "txt") {
            val tempFilePath = "E:/newFile/temp.txt"
            File(tempFilePath).writeText(text)

            val inputStream: InputStream = FileInputStream(tempFilePath)
            inputStream.use { s3Transfer.readInputStreamUsingFileChannel(it, path) }

            File(tempFilePath).delete()
        } else {
            val inputStream: InputStream = channel.get(remoteFilePath)
            inputStream.use { s3Transfer.readInputStreamUsingFileChannel(it, path) }
        }

        channel.disconnect()
        sshSession.disconnect()

    }
}