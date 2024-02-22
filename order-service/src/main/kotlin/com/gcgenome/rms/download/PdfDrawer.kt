package com.gcgenome.rms.download

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component


@Component
class PdfDrawer(private val resourceLoader: ResourceLoader,) {
    fun loadPdfDocument(service: String): PDDocument {
        val pdfResource = resourceLoader.getResource(service)
        val pdfFile = pdfResource.file
        return PDDocument.load(pdfFile)
    }

    fun getPageContentStream(document: PDDocument): PDPageContentStream {
        val page = document.getPage(0)
        return PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)
    }

    fun loadFont(document: PDDocument): PDType0Font {
        val fontGCStream = resourceLoader.getResource("classpath:font/GC120.ttf").inputStream
        return PDType0Font.load(document, fontGCStream)
    }

    fun addTextToPdf(contentStream: PDPageContentStream, text: Any?, x: Float, y: Float, font: PDType0Font, fontSize: Float) {
        contentStream.beginText()
        contentStream.setFont(font, fontSize)
        contentStream.newLineAtOffset(x, y)
        when (text) {
            is String -> contentStream.showText(text)
            is Int -> contentStream.showText(text.toString()) }
        contentStream.endText()
    }


}