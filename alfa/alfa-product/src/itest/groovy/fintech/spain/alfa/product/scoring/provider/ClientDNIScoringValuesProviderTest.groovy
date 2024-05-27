package fintech.spain.alfa.product.scoring.provider

import fintech.TimeMachine
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

import static fintech.BigDecimalUtils.amount

class ClientDNIScoringValuesProviderTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.scoring.ClientDNIScoringValuesProvider clientScoringValuesProvider

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
        def clientProps = clientScoringValuesProvider.provide(client.clientId)

        then:
        clientProps['document_number'] == client.dni
    }
}
