package com.gcgenome.rms.order

import com.gcgenome.lims.tables.references.SAMPLE_EXTENSION
import com.gcgenome.rms.data.Extension_
import com.gcgenome.rms.data.Sample_
import com.gcgenome.rms.entity.SampleExtension
import com.gcgenome.rms.repo.ExtensionRepository
import org.jooq.Configuration
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.ExtensionDao")
class ExtensionDao(
    val extensionRepo: ExtensionRepository
) {
    fun insertSampleExtension(trx: Configuration, sampleExtension: Extension_, sampleId: UUID) = trx.dsl()
        .insertInto(SAMPLE_EXTENSION)
        .columns(SAMPLE_EXTENSION.EXTENSION_ID, SAMPLE_EXTENSION.SAMPLE_ID, SAMPLE_EXTENSION.VALUE)
        .values(sampleExtension.id, sampleId, sampleExtension.value)
        .returning()
    fun deleteExtension(sampleId: UUID): Mono<Void> =
        extensionRepo.deleteAllBySampleId(sampleId)

    fun existExtension(sampleId: UUID): Mono<Boolean> =
        extensionRepo.existsBySampleId(sampleId)
}