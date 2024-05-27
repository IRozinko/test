package fintech.cms

import fintech.cms.spi.CmsItem
import fintech.cms.spi.CmsItemType

class CmsRegistryTest extends BaseSpecification {

    def "Register"() {
        when:
        registry.saveItem(new CmsItem(key: "Agreement", itemType: CmsItemType.PDF_HTML, contentTemplate: "Hello", scope: "client, loan", locale: "en"), false)
        registry.saveItem(new CmsItem(key: "Agreement", itemType: CmsItemType.PDF_HTML, contentTemplate: "Hola", scope: "client, loan", locale: "es"), false)

        then:
        with(registry.findItem(CmsItemType.PDF_HTML, "Agreement", "en").get()) {
            scope == "client, loan"
            contentTemplate == "Hello"
        }
        registry.findItem(CmsItemType.PDF_HTML, "Agreement", "es").get().contentTemplate == "Hola"
        registry.findItem(CmsItemType.PDF_HTML, "Agreement", "en").get().contentTemplate == "Hello"


        when: "Overwrite"
        registry.saveItem(new CmsItem(key: "Agreement", itemType: CmsItemType.PDF_HTML, contentTemplate: "Hello!", scope: "client, loan", locale: "en"), true)

        then:
        registry.findItem(CmsItemType.PDF_HTML, "Agreement", "en").get().contentTemplate == "Hello!"
        registry.findItem(CmsItemType.PDF_HTML, "Agreement", "es").get().contentTemplate == "Hola"

        when: "No overwrite"
        registry.saveItem(new CmsItem(key: "Agreement", itemType: CmsItemType.PDF_HTML, contentTemplate: "Hello!!!", scope: "client, loan", locale: "en"), false)

        then:
        registry.findItem(CmsItemType.PDF_HTML, "Agreement", "en").get().contentTemplate == "Hello!"
        registry.findItem(CmsItemType.PDF_HTML, "Agreement", "es").get().contentTemplate == "Hola"
    }

    def "Delete"() {
        when:
        registry.saveItem(new CmsItem(key: "Agreement", itemType: CmsItemType.PDF_HTML, contentTemplate: "Hello!", scope: "client, loan", locale: "es"), true)

        then:
        registry.findItem("Agreement", "es").isPresent()

        when:
        registry.deleteItem("Agreement")

        then:
        !registry.findItem("Agreement", "es").isPresent()
    }
}
