package com.gcgenome.rms.download

import com.gcgenome.rms.data.download.Nipt
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream


@Component
class NiptHandler(private val pdfDrawer: PdfDrawer) {
    fun niptDownload(userId: String, data: Nipt): Mono<ByteArray> {
        val document = pdfDrawer.loadPdfDocument("classpath:pdf/nipt.pdf")
        val contentStream = pdfDrawer.getPageContentStream(document)
        val fontGC120 = pdfDrawer.loadFont(document)

        pdfDrawer.addTextToPdf(contentStream, data.name, 195f, 723f, fontGC120, 9f)
        pdfDrawer.addTextToPdf(contentStream, data.mrn, 500f, 723f, fontGC120, 9f)
        pdfDrawer.addTextToPdf(contentStream, data.birth, 195f, 706.5f, fontGC120, 9f)
//        addCheckMarkByEthnicity(contentStream, data.ethnicity,689f, fontGC120, 9f)

        contentStream.close()
        val outputStream = ByteArrayOutputStream()
        document.save(outputStream)
        document.close()

        return Mono.just(outputStream.toByteArray())
    }

    fun addCheckMarkByEthnicity(contentStream: PDPageContentStream, ethnicity: String, y: Float, font: PDType0Font, fontSize: Float) {
        contentStream.beginText()
        contentStream.setFont(font, fontSize)
        val xOffset = when (ethnicity) {
            "EastAsian" -> 195f
            "SoutheastAsian" -> 205f
            "African" -> 215f
            "Caucasian" -> 225f
            "Hispanic" -> 235f
            "Other" -> 245f
            else -> throw IllegalArgumentException("error type: $ethnicity")
        }
        contentStream.newLineAtOffset(xOffset.toFloat(), y)
        when (ethnicity) {
            "EastAsian", "SoutheastAsian", "African", "Caucasian", "Hispanic", "Other" -> {
                contentStream.showText("✓")
            }
        }
        contentStream.endText()
    }
}