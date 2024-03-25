package com.gcgenome.rms.download

import com.gcgenome.rms.data.download.GenomeHealth
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream


@Component
class GenomeHealthHandler(private val pdfDrawer: PdfDrawer) {
    fun genomeHealthDownload(userId: String, data: GenomeHealth): Mono<ByteArray> {
        val document = pdfDrawer.loadPdfDocument("classpath:pdf/genome-health.pdf")
        val contentStream = pdfDrawer.getPageContentStream(document)
        val fontGC120 = pdfDrawer.loadFont(document)

        pdfDrawer.addTextToPdf(contentStream, data.name, 195f, 723f, fontGC120, 9f)
        pdfDrawer.addTextToPdf(contentStream, data.mrn, 500f, 723f, fontGC120, 9f)
        pdfDrawer.addTextToPdf(contentStream, data.birth, 195f, 706.5f, fontGC120, 9f)

        contentStream.close()
        val outputStream = ByteArrayOutputStream()
        document.save(outputStream)
        document.close()

        return Mono.just(outputStream.toByteArray())
    }
}