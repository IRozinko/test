package fintech.bo.api.server.services

import fintech.JsonUtils
import fintech.bo.api.model.product.UpdateProductSettingsRequest
import fintech.lending.core.product.ProductService
import fintech.lending.core.product.ProductType
import fintech.lending.core.product.db.ProductEntity
import fintech.lending.core.product.db.ProductRepository
import fintech.lending.creditline.settings.CreditLineOfferSettings
import fintech.lending.creditline.settings.CreditLinePricingSettings
import fintech.lending.creditline.settings.CreditLineProductSettings
import fintech.lending.payday.settings.PaydayOfferSettings
import fintech.lending.payday.settings.PaydayProductSettings
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Subject

class ProductApiServiceTest extends AbstractBaseSpecification {

    @Subject
    @Autowired
    ProductApiService productApiService

    @Autowired
    ProductService productService

    @Autowired
    ProductRepository productRepository

    @Autowired
    TransactionTemplate txTemplate

    def setup() {
        testDatabase.cleanDb()
    }

    def "update product settings with empty string"() {
        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 1, productType: ProductType.PAYDAY, settingsJson: ""))

        then:
        thrown(IllegalStateException)
    }

    def "update product settings with not valid product type"() {
        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 1, productType: -1, settingsJson: "{}"))

        then:
        thrown(IllegalArgumentException)
    }

    def "update not existing product settings"() {
        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 1, productType: ProductType.LINE_OF_CREDIT, settingsJson: "{}"))

        then:
        thrown(JpaObjectRetrievalFailureException)
    }

    def "update PAYDAY product settings with wrong product type"() {
        given:
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 2, productType: ProductType.PAYDAY,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new PaydayProductSettings(
                        publicOfferSettings: new PaydayOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        clientOfferSettings: new PaydayOfferSettings(minAmount: 20.00, maxAmount: 100.00)
                    ))))
        }

        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 2, productType: ProductType.LINE_OF_CREDIT, settingsJson: """
            { 
                "publicOfferSettings": {
                    "minAmount": 11.0
                },
                "clientOfferSettings": {}
            }
        """))

        then:
        thrown(IllegalStateException)
    }

    def "update LINE OF CREDIT product settings with wrong product type"() {
        given:
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 1, productType: ProductType.LINE_OF_CREDIT,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new CreditLineProductSettings(
                        offerSettings: new CreditLineOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        pricingSettings: new CreditLinePricingSettings()
                    ))))
        }

        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 1, productType: ProductType.PAYDAY, settingsJson: """
             { 
                "offerSettings": {
                    "minAmount": 11.00
                }
            }
        """))

        then:
        thrown(IllegalStateException)
    }

    def "update PAYDAY product settings with wrong product id"() {
        given:
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 1, productType: ProductType.LINE_OF_CREDIT,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new CreditLineProductSettings(
                        offerSettings: new CreditLineOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        pricingSettings: new CreditLinePricingSettings()
                    ))))
            productRepository.save(new ProductEntity(id: 2, productType: ProductType.PAYDAY,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new PaydayProductSettings(
                        publicOfferSettings: new PaydayOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        clientOfferSettings: new PaydayOfferSettings(minAmount: 20.00, maxAmount: 100.00)
                    ))))
        }

        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 1, productType: ProductType.PAYDAY, settingsJson: """
            { 
                "publicOfferSettings": {
                    "minAmount": 11.0
                },
                "clientOfferSettings": {}
            }
        """))

        then:
        thrown(IllegalArgumentException)
    }

    def "update LINE OF CREDIT product settings with wrong product id"() {
        given:
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 1, productType: ProductType.LINE_OF_CREDIT,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new CreditLineProductSettings(
                        offerSettings: new CreditLineOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        pricingSettings: new CreditLinePricingSettings()
                    ))))
            productRepository.save(new ProductEntity(id: 2, productType: ProductType.PAYDAY,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new PaydayProductSettings(
                        publicOfferSettings: new PaydayOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        clientOfferSettings: new PaydayOfferSettings(minAmount: 20.00, maxAmount: 100.00)
                    ))))
        }

        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 2, productType: ProductType.LINE_OF_CREDIT, settingsJson: """
            { 
                "offerSettings": {
                    "minAmount": 11.00
                }
            }
        """))

        then:
        thrown(IllegalArgumentException)
    }

    def "update PAYDAY product settings"() {
        given:
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 1, productType: ProductType.LINE_OF_CREDIT,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new CreditLineProductSettings(
                        offerSettings: new CreditLineOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        pricingSettings: new CreditLinePricingSettings()
                    ))))
            productRepository.save(new ProductEntity(id: 2, productType: ProductType.PAYDAY,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new PaydayProductSettings(
                        publicOfferSettings: new PaydayOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        clientOfferSettings: new PaydayOfferSettings(minAmount: 20.00, maxAmount: 100.00)
                    ))))
        }

        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 2, productType: ProductType.PAYDAY, settingsJson: """
            { 
                "publicOfferSettings": {
                    "minAmount": 11.0
                },
                "clientOfferSettings": {}
            }
        """))

        then:
        noExceptionThrown()

        when:
        def settings = productService.getSettings(2, PaydayProductSettings.class)

        then:
        settings
        settings.publicOfferSettings.minAmount == 11.0
        settings.clientOfferSettings
    }

    def "update LINE OF CREDIT product settings"() {
        given:
        txTemplate.execute {
            productRepository.save(new ProductEntity(id: 1, productType: ProductType.LINE_OF_CREDIT,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new CreditLineProductSettings(
                        offerSettings: new CreditLineOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        pricingSettings: new CreditLinePricingSettings()
                    ))))
            productRepository.save(new ProductEntity(id: 2, productType: ProductType.PAYDAY,
                defaultSettingsJson: JsonUtils.writeValueAsString(
                    new PaydayProductSettings(
                        publicOfferSettings: new PaydayOfferSettings(minAmount: 10.00, maxAmount: 99.00),
                        clientOfferSettings: new PaydayOfferSettings(minAmount: 20.00, maxAmount: 100.00)
                    ))))
        }

        when:
        productApiService.update(new UpdateProductSettingsRequest(productId: 1, productType: ProductType.LINE_OF_CREDIT, settingsJson: """
            { 
                "offerSettings": {
                    "minAmount": 11.00
                }
            }
        """))

        then:
        noExceptionThrown()

        when:
        def settings = productService.getSettings(1, CreditLineProductSettings.class)

        then:
        settings
        settings.offerSettings.minAmount == 11.0
    }
}
