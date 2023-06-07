package com.gcgenome.rms.order

import com.gcgenome.rms.data.Extension_
import com.gcgenome.rms.data.Sample_
import com.gcgenome.rms.entity.SampleExtension
import com.gcgenome.rms.repo.ExtensionRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository("com.gcgenome.rms.order.ExtensionDao")
class ExtensionDao(
    val extensionRepo: ExtensionRepository
) {
    fun saveExtension(sampleDto: Sample_, extensionDto: Extension_): Mono<Extension_> = extensionRepo.save(map(sampleDto, extensionDto)).map { entity->map(entity) }

    fun deleteExtension(sampleId: UUID): Mono<Void> =
        extensionRepo.deleteAllBySampleId(sampleId)

    fun existExtension(sampleId: UUID): Mono<Boolean> =
        extensionRepo.existsBySampleId(sampleId)
    fun map(sampleDto: Sample_, extensionDto: Extension_) = SampleExtension(
        sampleId = sampleDto.id!!,
        extensionId = extensionDto.id!!,
        value = extensionDto.value!!
    )
    private fun map(entity: SampleExtension) = Extension_(
        sampleId = entity.sampleId,
        id = entity.extensionId,
        value = entity.value
    )
}