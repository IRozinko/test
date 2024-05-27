package fintech.cms.impl.itext;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.IOException;

public class HeaderFooter extends PdfPageEventHelper {

    protected ElementList header;
    protected ElementList footer;

    public HeaderFooter(String headerValue, String footerValue) throws IOException {
        header = XMLWorkerHelper.parseToElementList(headerValue == null ? "" : headerValue, null);
        footer = XMLWorkerHelper.parseToElementList(footerValue == null ? "" : footerValue, null);
    }
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            ColumnText ct = new ColumnText(writer.getDirectContent());
            Rectangle pageSize = document.getPageSize();
            float left = document.leftMargin();
            float right = document.rightMargin();
            float top = document.topMargin();
            float bottom = document.bottomMargin();

            Rectangle headerRectangle = new Rectangle(
                pageSize.getLeft() + left, pageSize.getTop() - top,
                pageSize.getRight() - right, pageSize.getTop());
            ct.setSimpleColumn(headerRectangle);
            Rectangle footerRectangle = new Rectangle(
                pageSize.getLeft() + left, pageSize.getBottom(),
                pageSize.getRight() - right, pageSize.getBottom() + bottom);
            for (Element e : header) {
                ct.addElement(e);
            }
            ct.go();
            ct.setSimpleColumn(footerRectangle);
            for (Element e : footer) {
                ct.addElement(e);
            }
            ct.go();
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
}
