package fintech.spain.alfa.product.scoring.provider

import fintech.TimeMachine
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

import static fintech.BigDecimalUtils.amount

class ClientScoringValuesProviderTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.scoring.ClientScoringValuesProvider clientScoringValuesProvider

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
        when:
        def clientProps = clientScoringValuesProvider.provide(client.clientId)

        then:
        clientProps['user_data_email'] == client.email
        clientProps['user_data_address_zipcode'] == client.primaryAddress.postalCode
        clientProps['user_data_accept_marketing'] == "true"
    }
}
