package fintech.lending.core.product

import fintech.JsonUtils
import fintech.lending.BaseSpecification
import fintech.lending.core.product.db.ProductEntity
import fintech.lending.core.product.db.ProductRepository
import fintech.lending.creditline.settings.CreditLineOfferSettings
import fintech.lending.creditline.settings.CreditLinePricingSettings
import fintech.lending.creditline.settings.CreditLineProductSettings
import fintech.lending.payday.settings.PaydayOfferSettings
import fintech.lending.payday.settings.PaydayProductSettings
import fintech.lending.revolving.settings.RevolvingInvoiceSettings
import fintech.lending.revolving.settings.RevolvingPricingSettings
import fintech.lending.revolving.settings.RevolvingProductSettings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Subject
import spock.lang.Unroll

class ProductServiceTest extends BaseSpecification {

    @Subject
    @Autowired
    ProductService productService

    @Autowired
    ProductRepository productRepository

    @Autowired
    TransactionTemplate txTemplate

    def setup() {
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 1, productType: ProductType.LINE_OF_CREDIT,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new CreditLineProductSettings(
                        offerSettings: new CreditLineOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        pricingSettings: new CreditLinePricingSettings()
                    ))))
        }
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 2, productType: ProductType.PAYDAY,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new PaydayProductSettings(
                        publicOfferSettings: new PaydayOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        clientOfferSettings: new PaydayOfferSettings(minAmount: 20.00, maxAmount: 100.00)
                    ))))
        }
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 3, productType: ProductType.REVOLVING,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new RevolvingProductSettings(
                        pricingSettings: new RevolvingPricingSettings(),
                        invoiceSettings: new RevolvingInvoiceSettings(dueDays: 0, gracePeriod: 5)
                    ))))
        }
    }

    def "get product"() {
        when:
        def result = productService.getProduct(productId)

        then:
        result
        result.productType == productType

        where:
        productId | productType
        1         | ProductType.LINE_OF_CREDIT
        2         | ProductType.PAYDAY
        3         | ProductType.REVOLVING
    }

    @Unroll
    def "get wrong settings, #productClassSettings - #productId"() {
        when:
        productService.getSettings(productId, productSettingsClass)

        then:
        thrown(IllegalStateException)

        where:
        productId | productSettingsClass
        2         | CreditLineProductSettings.class
        1         | RevolvingProductSettings.class
        3         | PaydayProductSettings.class
    }

    def "get/update RVL settings"() {
        when:
        def settings = productService.getSettings(3, RevolvingProductSettings.class)

        then:
        settings
        settings.invoiceSettings.dueDays == 0
        settings.invoiceSettings.gracePeriod == 5

        when:
        productService.updateSettings(3, new RevolvingProductSettings(
            invoiceSettings: new RevolvingInvoiceSettings(dueDays: 3, gracePeriod: 0)
        ))

        and:
        settings = productService.getSettings(3, RevolvingProductSettings.class)

        then:
        settings
        settings.invoiceSettings.dueDays == 3
        settings.invoiceSettings.gracePeriod == 0
    }

    def "get/update LOC settings"() {
        when:
        def settings = productService.getSettings(1, CreditLineProductSettings.class)

        then:
        settings
        settings.offerSettings.minAmount == 10.00
        settings.offerSettings.maxAmount == 99.00

        when:
        productService.updateSettings(1, new CreditLineProductSettings(
            offerSettings: new CreditLineOfferSettings(minAmount: 5.00, maxAmount: 30.00)
        ))

        and:
        settings = productService.getSettings(1, CreditLineProductSettings.class)

        then:
        settings
        settings.offerSettings.minAmount == 5.00
        settings.offerSettings.maxAmount == 30.00
    }

    def "get/update PDL settings"() {
        when:
        def settings = productService.getSettings(2, PaydayProductSettings.class)

        then:
        settings
        with(settings) {
            publicOfferSettings.minAmount == 10.00
            publicOfferSettings.maxAmount == 99.00
            clientOfferSettings.minAmount == 20.00
            clientOfferSettings.maxAmount == 100.00
        }

        when:
        productService.updateSettings(2, new PaydayProductSettings(
            publicOfferSettings: new PaydayOfferSettings(minAmount: 9.00, maxAmount: 98.00),
            clientOfferSettings: new PaydayOfferSettings(minAmount: 21.00, maxAmount: 101.00)
        ))

        and:
        settings = productService.getSettings(2, PaydayProductSettings.class)

        then:
        settings
        with(settings) {
            publicOfferSettings.minAmount == 9.00
            publicOfferSettings.maxAmount == 98.00
            clientOfferSettings.minAmount == 21.00
            clientOfferSettings.maxAmount == 101.00
        }
    }

    def "parse wrong settings"() {
        given:
        def json = productRepository.getRequired(productId).defaultSettingsJson

        when:
        productService.parseSettings(json, productSettingsClass)

        then:
        thrown(IllegalStateException)

        where:
        productId | productSettingsClass
        2L        | CreditLineProductSettings.class
        1L        | RevolvingProductSettings.class
        3L        | PaydayProductSettings.class
    }

    def "parse LOC settings"() {
        given:
        def json = productRepository.getRequired(1L).defaultSettingsJson

        when:
        def settings = productService.parseSettings(json, CreditLineProductSettings.class)

        then:
        settings
        settings.offerSettings.minAmount == 10.00
        settings.offerSettings.maxAmount == 99.00
    }

    def "parse PDL settings"() {
        given:
        def json = productRepository.getRequired(2L).defaultSettingsJson

        when:
        def settings = productService.parseSettings(json, PaydayProductSettings.class)

        then:
        settings
        with(settings) {
            publicOfferSettings.minAmount == 10.00
            publicOfferSettings.maxAmount == 99.00
            clientOfferSettings.minAmount == 20.00
            clientOfferSettings.maxAmount == 100.00
        }
    }

    def "parse RVL settings"() {
        given:
        def json = productRepository.getRequired(3L).defaultSettingsJson

        when:
        def settings = productService.parseSettings(json, RevolvingProductSettings.class)

        then:
        settings
        with(settings) {
            invoiceSettings.dueDays == 0
            invoiceSettings.gracePeriod == 5
        }
    }
}
