package fintech.cms

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfReaderContentParser
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy

class PdfUtil {

    static String readText(byte[] pdf, int pageNumber) {
        def reader = new PdfReader(pdf);
        def parser = new PdfReaderContentParser(reader);
        def strategy = parser.processContent(pageNumber, new SimpleTextExtractionStrategy());
        def text = strategy.getResultantText()
        reader.close()
        return text
    }
}
