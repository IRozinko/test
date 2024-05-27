package fintech.spain.alfa.product.scoring.provider

import fintech.TimeMachine
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

import static fintech.BigDecimalUtils.amount

class ApplicationPeriodScoringValuesProviderTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.scoring.ApplicationPeriodScoringValuesProvider provider

    def "Provide"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplication(new fintech.spain.alfa.product.lending.Inquiry()
                .setPrincipal(amount(300))
                .setInterestDiscountPercent(amount(0))
                .setSubmittedAt(TimeMachine.now())
                .setTermInDays(30L))
            .toClient()
            .cancelActiveApplication()
            .submitApplication(new fintech.spain.alfa.product.lending.Inquiry()
                .setPrincipal(amount(100))
                .setInterestDiscountPercent(amount(0))
                .setSubmittedAt(TimeMachine.now())
                .setTermInDays(10L))
            .toClient()
            .cancelActiveApplication()
            .submitApplicationAndStartFirstLoanWorkflow(amount(200), 15L, TimeMachine.today())

        when:
        def props = provider.provide(client.clientId)

        then:
        props['application_period_max'] == 30
        props['application_period_min'] == 10
        props['application_period_avg'] == 20
        props['application_period_std'] == 14.14
        props['application_period_last'] == 10

    }

}
