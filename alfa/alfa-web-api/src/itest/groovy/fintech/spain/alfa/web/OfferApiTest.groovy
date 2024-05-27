package fintech.spain.alfa.web

import fintech.lending.core.application.LoanApplicationQuery
import fintech.lending.core.application.LoanApplicationStatus
import fintech.spain.alfa.product.AlfaConstants
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.alfa.product.workflow.common.Resolutions
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows
import fintech.spain.web.common.ApiError
import org.springframework.http.HttpMethod

class OfferApiTest extends AbstractAlfaApiTest {

    def "Approve offer with long code (email)"() {
        given:
        def client = TestFactory.newClient()
        client.randomEmailAndName("First Loan Workflow Completed")
            .signUp()
            .toLoanWorkflow()
            .runBeforeActivity(UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER)

        expect:
        activeState(apiHelper.findWorkflow(client).get()).get().name == UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER

        when: "Client approves offer"
        def code = applicationService.findLatest(LoanApplicationQuery.byClientId(client.clientId, LoanApplicationStatus.OPEN)).get().longApproveCode
        def loginResponse = restTemplate.getForEntity("/api/public/web/approve-offer?code=" + code, fintech.spain.alfa.web.models.LoginResponse.class)

        then:
        loginResponse.statusCode.is2xxSuccessful()
        loginResponse.body.token
        getActivityResolution(apiHelper.findWorkflow(client).get(), UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER) == Resolutions.APPROVE

        when: "Client gets client-info"
        def clientInfo = restTemplate.exchange("/api/public/web/client", HttpMethod.GET,
            ApiHelper.authorized(loginResponse.body.token, ""), fintech.spain.alfa.web.models.ClientInfoResponse.class)

        then:
        clientInfo.statusCode.is2xxSuccessful()
        clientInfo.body.authenticated

        when: "Client tries to approve same offer one more time"
        def loginResponse2 = restTemplate.getForEntity("/api/public/web/approve-offer?code=" + code, ApiError.class)

        then:
        loginResponse2.statusCodeValue == 400
        with (loginResponse2.body) {
            message == "Bad request"
            fieldErrors.containsKey("code")
        }

    }

    def "Approve offer via incoming SMS"() {
        given:

        def client = TestFactory.newClient()
        client.randomEmailAndName("First Loan Workflow Completed")
            .signUp()
            .toLoanWorkflow()
            .run(UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER, null)

        expect:
        activeState(apiHelper.findWorkflow(client).get()).get().name == UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER

        when:
        def code = applicationService.findLatest(LoanApplicationQuery.byClientId(client.clientId, LoanApplicationStatus.OPEN)).get().shortApproveCode
        def result = restTemplate.postForEntity("/api/public/web/altiria-sms", ApiHelper.formPost([telnum: AlfaConstants.PHONE_COUNTRY_CODE + client.signUpForm.mobilePhone, keyword: code, text: code]), String.class)

        then:
        result.statusCode.is2xxSuccessful()

        then:
        conditions.eventually {
            getActivityResolution(apiHelper.findWorkflow(client).get(), UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER) == Resolutions.APPROVE
        }
    }
}
