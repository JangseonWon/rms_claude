package com.gcgenome.rms.data

import java.util.*

data class RmsSampleExtension(
    val extensionId: String,
    val sampleId: UUID,
    val value: String?
)
