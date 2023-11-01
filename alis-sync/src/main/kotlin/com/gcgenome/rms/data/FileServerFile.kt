package com.gcgenome.rms.data

data class FileServerFile (
    val fileName: String,
    val type: String,
    val windowPath: String,
    val s3Path: String,
    val text: String,
    val longText: Boolean,
    val shortText: Boolean,
    val sequence: Number,
    val genomeBarcode: String,
    val serviceCode: String
) {
    companion object {
        fun convertFileServerFile(alisOrderFile: AlisOrderFile): FileServerFile {
            val alisPath = alisOrderFile.filePath.split("\\")
            val branchCode = alisOrderFile.orderNumber.toString().substring(0,3)
            val postfix = (alisOrderFile.orderNumber%10000).toString().padStart(4, '0')
            val serviceCode = alisPath[alisPath.size - 4]
            val year = alisPath[alisPath.size - 3]
            val dayString = alisPath[alisPath.size - 2]
            val month = dayString.substring(0, 2)
            val day = dayString.substring(2, 4)
            val fileName = alisOrderFile.fileName
            val checkText = alisPath[2]
            val longText = checkText.contains("cTextReport")
            val shortText = checkText.contains("TextReport")
            val sequenceCheck = alisOrderFile.fileNameSeq.count { it == '_' } >= 2
            val sequence = if (sequenceCheck) { alisOrderFile.fileSeq + 1 } else { 0 }
            val type = if (longText || shortText) { "txt" } else { alisOrderFile.fileExt }
            val s3Path = "reports/$year/$month/$day/$branchCode/$postfix/$serviceCode/$fileName.$type"
            return FileServerFile (
                fileName = fileName,
                type = type,
                windowPath = alisOrderFile.filePath,
                s3Path = s3Path,
                text = alisOrderFile.textFile,
                longText,
                shortText,
                sequence = sequence,
                genomeBarcode = year + month + day + branchCode + postfix,
                serviceCode
            )
        }
    }
}