package com.gcgenome.rms.download

import com.gcgenome.rms.data.download.GenomeHealth
import com.gcgenome.rms.data.download.Nipt
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono


@Component
class RequestHandler(
    private val niptHandler: NiptHandler,
    private val genomeHealthHandler: GenomeHealthHandler
) {
    fun handleDownloadByService(principal: String, serviceName: String, request: ServerRequest): Mono<ByteArray> {
        return when (serviceName) {
            "Nipt" -> request.bodyToMono(Nipt::class.java)
                .flatMap { nipt ->
                    niptHandler.niptDownload(principal, nipt)
                }
            "GenomeHealth" -> request.bodyToMono(GenomeHealth::class.java)
                .flatMap { genomeHealth ->
                    genomeHealthHandler.genomeHealthDownload(principal, genomeHealth)
                }
            else -> Mono.error(IllegalArgumentException("Unsupported service: $serviceName"))
        }
    }

    fun capitalizeServiceName(service: String): String {
        return when (service) {
            "nipt" -> "Nipt"
            "genomehealth" -> "GenomeHealth"
            else -> service
        }
    }
}