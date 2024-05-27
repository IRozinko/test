package fintech.spain.alfa.product.scoring.provider

import fintech.TimeMachine
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

import static fintech.BigDecimalUtils.amount

class ApplicationPrincipalScoringValuesProviderTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.scoring.ApplicationPrincipalScoringValuesProvider provider

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
        props['application_principal_max'] == 300
        props['application_principal_min'] == 100
        props['application_principal_avg'] == 200
        props['application_principal_std'] == 141.42
        props['application_principal_last'] == 100
    }
}
