package fintech.cms

import fintech.ClasspathUtils
import fintech.cms.spi.CmsItem
import fintech.cms.spi.CmsItemType
import org.springframework.beans.factory.annotation.Autowired

class PdfRenderTest extends BaseSpecification {

    @Autowired
    PdfRenderer pdfRenderer

    def "Render PDF"() {
        given:
        registerTemplate()
        def context = [client: [firstName: "John"], loan: [number: "123456"]]

        when: "Render English texts"
        def pdf = pdfRenderer.render("Agreement", context, "en").get()

        then:
        pdf.name == "agreement_123456.pdf"
        pdf.content.length > 0
        PdfUtil.readText(pdf.content, 1).contains("This is your loan 123456 agreement")

        when: "Render Latvian texts"
        pdf = pdfRenderer.render("Agreement", context, "lv").get()

        then:
        pdf.name == "ligums_123456.pdf"
        pdf.content.length > 0
        PdfUtil.readText(pdf.content, 1).contains("Jusu ligums 123456")
    }

    def "Ignore unknown template key"() {
        expect:
        !pdfRenderer.render("unknown", [:], "en").isPresent()
    }

    private void registerTemplate() {
        registry.saveItem(new CmsItem(key: "Agreement", itemType: CmsItemType.PDF_HTML, titleTemplate: "agreement_{{loan.number}}.pdf", contentTemplate: ClasspathUtils.resourceToString("agreement-template-en.html"), scope: "client, loan", locale: "es"), true)
        registry.saveItem(new CmsItem(key: "Agreement", itemType: CmsItemType.PDF_HTML, titleTemplate: "ligums_{{loan.number}}.pdf", contentTemplate: ClasspathUtils.resourceToString("agreement-template-lv.html"), scope: "client, loan", locale: "lv"), true)
    }
}
