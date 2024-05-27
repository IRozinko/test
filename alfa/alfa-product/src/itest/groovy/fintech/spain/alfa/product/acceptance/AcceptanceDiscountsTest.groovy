package fintech.spain.alfa.product.acceptance

import fintech.spain.alfa.product.AbstractAlfaTest

import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date

class AcceptanceDiscountsTest extends AbstractAlfaTest {

    @Autowired
    DiscountTestCases discountTestCases

    def "loan 100(0), 100(0), 99(0) -> 0%"() {
        expect:
        assert discountTestCases.discount(100.00, 0 ,100.00, 0, 99.00, 0, 15.00)
            .offerSettings(date("2018-03-01"))
            .interestDiscountPercent == 0.00
    }

    def "loan 100(0), 100(0), 100(0) -> 15%"() {
        expect:
        assert discountTestCases.discount(100.00, 0 ,100.00, 0, 100.00, 0, 15.00)
            .offerSettings(date("2018-03-01"))
            .interestDiscountPercent == 15.00
    }

    def "loan 300(0), 300(0), 300(0) -> 20%"() {
        expect:
        assert discountTestCases.discount(300.00, 0 ,300.00, 0, 300.00, 0, 20.00)
            .offerSettings(date("2018-03-01"))
            .interestDiscountPercent == 20.00
    }

    def "loan 300(0), 300(3), 300(0) -> 15%"() {
        expect:
        assert discountTestCases.discount(300.00, 0 ,300.00, 3, 300.00, 0, 15.00)
            .offerSettings(date("2018-03-01"))
            .interestDiscountPercent == 15.00
    }

    def "loan 300(0), 300(3), 299(0) -> 0%"() {
        expect:
        assert discountTestCases.discount(300.00, 0 ,300.00, 3, 299.00, 0, 15.00)
            .offerSettings(date("2018-03-01"))
            .interestDiscountPercent == 0.00
    }

    def "loan 300(0), 300(0), 300(3) -> 0%"() {
        expect:
        assert discountTestCases.discount(300.00, 0 ,300.00, 0, 300.00, 3, 15.00)
            .offerSettings(date("2018-03-01"))
            .interestDiscountPercent == 0.00
    }

    def "loan 300(3), 300(0), 450(0) -> 20%"() {
        expect:
        assert discountTestCases.discount(300.00, 3 ,300.00, 0, 450.00, 0, 20.00)
            .offerSettings(date("2018-03-01"))
            .interestDiscountPercent == 20.00
    }

    def "loan 500(0), 500(0), 500(0) -> 25%"() {
        expect:
        assert discountTestCases.discount(500.00, 0 ,500.00, 0, 500.00, 0, 25.00)
            .offerSettings(date("2018-03-01"))
            .interestDiscountPercent == 25.00
    }

}
