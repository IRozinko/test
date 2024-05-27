package fintech.spain.alfa.web.navigation

import fintech.instantor.InstantorService
import fintech.instantor.InstantorSimulation
import fintech.spain.platform.web.spi.SpecialLinkService


import fintech.spain.alfa.product.testing.RandomData
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow
import fintech.spain.alfa.web.AbstractAlfaApiTest
import fintech.spain.alfa.web.ClientApiHelper
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import spock.lang.Ignore

import static fintech.spain.platform.web.model.command.SpecialLinkQuery.byClientId

class ChangeBankAccountWorkflowTest extends AbstractAlfaApiTest {

    @Autowired
    SpecialLinkService linkService

    @Autowired
    ClientApiHelper clientApiHelper

    @Autowired
    InstantorService instantorService

    @Autowired
    InstantorFacade instantorFacade

    @Ignore
    def "Change bank account - straight scenario"() {
        given:
        def newBankAccount = RandomData.randomIban().toString()
        def client = TestFactory.newClient()
            .registerDirectly()
            .submitChangeBankAccountWorkflow()

        when:
        def workflow = client.toChangeBankAccountWorkflow()

        then:
        assert workflow.isActivityActive(ChangeBankAccountWorkflow.Activities.CHANGE_BANK_ACCOUNT_LINK)

        when:
        def link = linkService.findLink(byClientId(client.clientId, ChangeBankAccountWorkflow.CHANGE_BANK_ACCOUNT_LINK)).get()
        def response = restTemplate.postForEntity("/api/public/web/special-link/activate/" + link.token, null, fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        assert response.statusCode.is2xxSuccessful()

        when:
        def webtoken = response.getBody().token
        def clientInfo = clientApiHelper.getClientInfo(webtoken)

        then:
        assert clientInfo.getState() == fintech.spain.alfa.web.services.navigation.UiState.CHANGE_BANK_ACCOUNT_VERIFY
        assert workflow.isActivityActive(ChangeBankAccountWorkflow.Activities.CHANGE_BANK_ACCOUNT_VERIFY)

        when:
        response = restTemplate.exchange("/api/web/registration/instantor-form-completed", HttpMethod.POST, fintech.spain.alfa.web.ApiHelper.authorized(webtoken, ""), Object.class)

        then:
        assert response.statusCode.is2xxSuccessful()

        when:
        clientInfo = clientApiHelper.getClientInfo(webtoken)

        then:
        assert clientInfo.getState() == fintech.spain.alfa.web.services.navigation.UiState.CHANGE_BANK_ACCOUNT_IN_PROGRESS
        assert workflow.isActivityActive(ChangeBankAccountWorkflow.Activities.CHANGE_BANK_ACCOUNT_INSTANTOR_CALLBACK)

        when:
        def responseId = instantorService.saveResponse(InstantorSimulation.simulateOkResponse(
            client.clientId,
            client.getDni(),
            StringUtils.join(client.firstName, client.lastName, client.secondLastName),
            newBankAccount,
            RandomData.randomIban().toString()
        ))
        instantorService.processResponse(responseId)

        and:
        clientInfo = clientApiHelper.getClientInfo(webtoken)

        then:
        assert clientInfo.getState() == fintech.spain.alfa.web.services.navigation.UiState.CHANGE_BANK_ACCOUNT_CHOICE
        assert workflow.isActivityActive(ChangeBankAccountWorkflow.Activities.CHANGE_BANK_ACCOUNT_CHOICE)

        when:
        response = restTemplate.exchange("/api/web/registration/instantor-review", HttpMethod.GET, fintech.spain.alfa.web.ApiHelper.authorized(webtoken, ""), InstantorReviewResponse.class)

        then:
        assert response.statusCode.is2xxSuccessful()
        def reviewResponse = response.getBody()
        assert reviewResponse.getAccounts().size() == 2
        assert reviewResponse.getAccounts().stream().filter {
            it.bankAccountNumber == newBankAccount
        }.findAny().isPresent()

        when:
        def request = new CompleteInstantorReviewRequest()
        request.setBankAccountNumber(newBankAccount)
        request.setValidateBankAccountNumber(true)
        response = restTemplate.exchange("/api/web/registration/instantor-review-completed", HttpMethod.POST, fintech.spain.alfa.web.ApiHelper.authorized(webtoken, request), Object.class)

        and:
        clientInfo = clientApiHelper.getClientInfo(webtoken)

        then:
        assert response.statusCode.is2xxSuccessful()
        assert workflow.isCompleted()
        assert clientInfo.getState() == fintech.spain.alfa.web.services.navigation.UiState.PROFILE
    }

}
