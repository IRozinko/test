package fintech.spain.alfa.product.scoring.provider

import fintech.affiliate.AffiliateService
import fintech.affiliate.model.SaveAffiliateRequestCommand
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate

class AffiliateRequestScoringValuesProviderTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.scoring.AffiliateRequestScoringValuesProvider provider
    @Autowired
    fintech.spain.alfa.product.affiliate.AffiliateRegistrationFacade affiliateRegistrationFacade
    @Autowired
    AffiliateService affiliateService
    @Autowired
    TransactionTemplate txTemplate

    def "Provide"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        def form = client.affiliateRegistrationStep1FormV1
        fintech.spain.alfa.product.affiliate.AffiliateRegistrationFacade.AffiliateRegistrationResult result = txTemplate.execute {
            affiliateRegistrationFacade.step1V1('name', form)
        }
        affiliateService.saveAffiliateRequest(new SaveAffiliateRequestCommand()
            .setRequestType("step1V1")
            .setApplicationId(result.getApplicationId())
            .setClientId(result.getClientId())
            .setResponse("any")
            .setRequest(form))
        def props = provider.provide(result.clientId)

        then:
        props['affiliate_array_raw_json']

        def data = props['affiliate_array_raw_json']
        data['raw'].size() == 1
        data['raw'][0]['request_type'] == 'step1V1'
        data['raw'][0]['request']['IBAN'] == form.iban
    }

}
