package com.gcgenome.rms.extension

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.ExtensionDTO
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ExtensionHandler(
    val dslContext: DSLContext
):ExtensionDao, QueryDao {
    fun getAllExtensions(): Flux<ExtensionDTO> {
        return dslContext.selectExtensions()
    }

    fun searchExtensions(query: Query): Mono<Page<ExtensionDTO>> {
        return dslContext.selectExtensionsWithPage(query)
    }
    fun getExtensionById(extensionId: String): Mono<ExtensionDTO> {
        return dslContext.selectExtensionById(extensionId)
    }

    fun updateExtensionById(extension: ExtensionDTO): Mono<ExtensionDTO> {
        return dslContext.updateExtension(extension)
    }
}