package fintech.spain.alfa.product.strategy.extension

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties
import fintech.strategy.model.ExtensionOffer
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static fintech.BigDecimalUtils.amount

class AlfaExtensionStrategyTest extends AbstractAlfaTest {

    public static final String DISBURSEMENTS_FILE_NAME = "ING_20180909164712_QW7.xml";

    @Autowired
    fintech.spain.alfa.product.extension.impl.ExtensionServiceBean extensionServiceBean

    @Unroll
    def 'calculates extension price'() {
        given:
        def issueDate = TimeMachine.now().toLocalDate()
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(principal, 10, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME).getLoan()
        when:
        List<ExtensionOffer> offers = new AlfaExtensionStrategy(
            loan,
            JsonUtils.toJsonNode(
                new ExtensionStrategyProperties()
                    .setExtensions(Arrays.asList(
                    new ExtensionStrategyProperties.ExtensionOption().setTerm(7L).setRate(amount(10.00)),
                    new ExtensionStrategyProperties.ExtensionOption().setTerm(14L).setRate(amount(14.00)),
                    new ExtensionStrategyProperties.ExtensionOption().setTerm(30L).setRate(amount(26.00)),
                    new ExtensionStrategyProperties.ExtensionOption().setTerm(45L).setRate(amount(35.00))
                ))
            ),
            extensionDiscountService
        ).getOffers(TimeMachine.today())

        then:
        assert offers[0].price == _7
        assert offers[1].price == _14
        assert offers[2].price == _30
        assert offers[3].price == _45

        where:
        principal  | _7 | _14 | _30 | _45
        525.00     | 53 | 74  | 137 | 184
        524.00     | 52 | 73  | 136 | 183
    }

    @Unroll
    def 'calculates extension price with discount'() {
        given:
        def issueDate = TimeMachine.now().toLocalDate()
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueLoan(principal, 10, issueDate)
            .exportDisbursements(issueDate, DISBURSEMENTS_FILE_NAME).getLoan()
        extensionDiscountService.createExtensionDiscount(new fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand()
            .setLoanId(loan.getId())
            .setEffectiveFrom(LocalDate.now())
            .setEffectiveTo(LocalDate.now().plusDays(30))
            .setRateInPercent(discount_percent));
        when:
        List<ExtensionOffer> offers = new AlfaExtensionStrategy(
            loan,
            JsonUtils.toJsonNode(
                new ExtensionStrategyProperties()
                    .setExtensions(Arrays.asList(
                        new ExtensionStrategyProperties.ExtensionOption().setTerm(7L).setRate(amount(10.00)),
                        new ExtensionStrategyProperties.ExtensionOption().setTerm(14L).setRate(amount(14.00)),
                        new ExtensionStrategyProperties.ExtensionOption().setTerm(30L).setRate(amount(26.00)),
                        new ExtensionStrategyProperties.ExtensionOption().setTerm(45L).setRate(amount(35.00))
                    ))
            ),
            extensionDiscountService
        ).getOffers(TimeMachine.today())

        then:
        assert offers[0].price == _7
        assert offers[0].priceWithDiscount == _7d
        assert offers[1].price == _14
        assert offers[1].priceWithDiscount == _14d
        assert offers[2].price == _30
        assert offers[2].priceWithDiscount == _30d
        assert offers[3].price == _45
        assert offers[3].priceWithDiscount == _45d

        where:
        principal  |  discount_percent   | _7 | _14 | _30 | _45 | _7d | _14d | _30d | _45d
        525.00     |         3.00        | 53 | 74  | 137 | 184 | 51  | 72   | 133  | 178
        524.00     |        30.00        | 52 | 73  | 136 | 183 | 36  | 51   | 95   | 128
    }

}
