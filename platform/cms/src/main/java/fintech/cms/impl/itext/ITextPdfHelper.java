package fintech.cms.impl.itext;

import com.google.common.base.Throwables;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

public class ITextPdfHelper {

    public static final String FONTS_OPEN_SANS_REGULAR_TTF = "fonts/OpenSans-Regular.ttf";
    public static final String FONTS_OPEN_SANS_BOLD_TTF = "fonts/OpenSans-Bold.ttf";

    public static byte[] generatePdf(String htmlCode, String header, String footer) {
        try {
            Document document = newDocument();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, os);
            document.open();
            XMLParser p = buildParser(document, writer, header, footer);
            p.parse(new StringReader(htmlCode));
            document.close();
            return addPageNumbers(os.toByteArray());
        } catch (DocumentException | IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private static Document newDocument() {
        Document document = new Document();
        document.setPageSize(PageSize.A4);
        document.setMargins(60, 60, 60, 60);
        return document;
    }

    private static XMLParser buildParser(Document document, PdfWriter writer, String header, String footer) throws IOException {
        CSSResolver cssResolver =
                XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
        XMLWorkerFontProvider fontProvider =
                new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
        fontProvider.register(new ClassPathResource(FONTS_OPEN_SANS_REGULAR_TTF).getPath(), "OpenSans-Regular");
        fontProvider.register(new ClassPathResource(FONTS_OPEN_SANS_BOLD_TTF).getPath(), "OpenSans-Bold");

        HtmlPipelineContext htmlContext = new HtmlPipelineContext(new CssAppliersImpl(fontProvider));
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
        htmlContext.setImageProvider(new Base64ImageProvider());

        writer.setPageEvent(new HeaderFooter(header, footer));
        PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

        XMLWorker worker = new XMLWorker(css, true);
        return new XMLParser(worker);
    }

    private static byte[] addPageNumbers(byte[] pdf) throws DocumentException, IOException {
        BaseFont baseFont = BaseFont.createFont(FONTS_OPEN_SANS_REGULAR_TTF, BaseFont.WINANSI, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 8);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(pdf);
        int pagesTotal = reader.getNumberOfPages();

        PdfStamper stamper = new PdfStamper(reader, os);
        for (int i = 1; i <= pagesTotal; i++) {
            PdfContentByte pageContent = stamper.getOverContent(i);
            ColumnText.showTextAligned(pageContent, Element.ALIGN_CENTER,
                    new Phrase(String.format("%s / %s", i, pagesTotal), font), (PageSize.A4.getWidth() / 2), 45, 0);
        }
        stamper.close();
        reader.close();
        return os.toByteArray();
    }
}
