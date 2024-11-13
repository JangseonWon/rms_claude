package com.gcgenome.rms.extension

import com.gcgenome.rms.dao.*
import com.gcgenome.rms.data.ExtensionDTO
import com.gcgenome.rms.data.Page
import com.gcgenome.rms.data.Query
import com.gcgenome.rms.exception.ExtensionNotFoundException
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ExtensionHandler(
    val dslContext: DSLContext
):ExtensionDao, QueryDao {

    fun selectExtensionsWithPage(query: Query): Mono<Page<ExtensionDTO>> {
        return dslContext.selectExtensionsWithPage(query)
    }
    fun selectExtension(extensionId: String): Mono<ExtensionDTO> {
        return dslContext.selectExtensionById(extensionId)
    }

    fun updateExtension(extension: ExtensionDTO): Mono<ExtensionDTO> {
        return dslContext.updateExtension(extension)
    }
}