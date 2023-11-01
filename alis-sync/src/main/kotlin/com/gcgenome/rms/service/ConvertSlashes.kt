package com.gcgenome.rms.service

import org.springframework.stereotype.Service

@Service
class ConvertSlashes {
    fun convertSlashesToBackslashes(input: String): String {
        return input.replace("/", "\\")
    }

    fun convertBackSlashesToSlashes(input: String): String {
        return input.replace("\\", "/")
    }
}