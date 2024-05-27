package fintech.spain.alfa.web

import fintech.affiliate.AffiliateService
import fintech.affiliate.db.AffiliateRequestRepository
import fintech.affiliate.db.Entities
import fintech.crm.client.db.ClientRepository
import fintech.crm.contacts.PhoneContactService
import fintech.crm.documents.IdentityDocumentService
import fintech.iovation.IovationService
import fintech.iovation.model.IovationBlackboxQuery
import fintech.iovation.model.SaveBlackboxCommand
import fintech.lending.core.application.LoanApplicationQuery
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.LoanApplicationSourceType
import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.risk.checklist.CheckListConstants
import fintech.risk.checklist.CheckListService
import fintech.risk.checklist.commands.AddCheckListEntryCommand
import fintech.spain.equifax.mock.MockEquifaxProvider
import fintech.spain.experian.impl.cais.MockExperianCaisProvider
import fintech.spain.alfa.product.AlfaConstants
import fintech.spain.alfa.product.affiliate.AffiliateRegistrationStep2Form
import fintech.spain.alfa.product.testing.TestFactory

import fintech.spain.alfa.product.workflow.common.Attributes
import fintech.spain.alfa.product.workflow.common.Resolutions
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows
import fintech.webanalytics.WebAnalyticsService
import fintech.webanalytics.model.WebAnalyticsEventQuery
import fintech.workflow.ActivityStatus
import fintech.workflow.WorkflowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import spock.lang.Unroll

import static fintech.DateUtils.date
import static fintech.spain.alfa.product.AlfaConstants.TEST_AFFILIATE_API_KEY
import static fintech.spain.alfa.product.affiliate.AffiliateApplicationStatus.ACCEPTED
import static fintech.spain.alfa.product.affiliate.AffiliateApplicationStatus.COMPLETED
import static fintech.spain.alfa.product.affiliate.AffiliateApplicationStatus.DECLINED
import static fintech.spain.alfa.product.affiliate.AffiliateApplicationStatus.FAILED
import static fintech.spain.alfa.product.affiliate.AffiliateApplicationStatus.NOT_FOUND
import static fintech.spain.alfa.product.affiliate.AffiliateApplicationStatus.PENDING
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.DOCUMENT_FORM
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EQUIFAX_RUN_1
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EXPERIAN_CAIS_OPERACIONES_RUN_1
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EXPERIAN_CAIS_RESUMEN_RUN_1
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.IOVATION_BLACKBOX_RUN_1

class AffiliateRegistrationApiTest extends AbstractAlfaApiTest {

    @Autowired
    TestFactory testFactory

    @Autowired
    ClientApiHelper clientApiHelper

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    IdentityDocumentService identityDocumentService

    @Autowired
    PhoneContactService phoneContactService

    @Autowired
    WorkflowService workflowService

    @Autowired
    AffiliateRequestRepository affiliateRequestRepository

    @Autowired
    AffiliateService affiliateService

    @Autowired
    CheckListService checkListService

    @Autowired
    IovationService iovationService

    @Autowired
    MockEquifaxProvider mockEquifaxProvider

    @Autowired
    MockExperianCaisProvider mockExperianCaisProvider

    @Autowired
    ClientRepository clientRepository

    @Autowired
    WebAnalyticsService webAnalyticsService

    def "registration V1"() {
        given:
        def client = TestFactory.newClient()
        affiliateRequestRepository.count() == 0

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.applicationUuid
        !step1Result.body.repeated
        !step1Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1
        def savedRequest = affiliateRequestRepository.findOne(Entities.request.clientId.isNotNull())
        with(savedRequest) {
            requestType == "step1V1"
            clientId != null
            applicationId != null
            request != null
            response != null
        }

        when: "can't send twice same request"
        def step1FailResult = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1FailResult.statusCodeValue == 422
        step1FailResult.body.message == "Form Affiliate Not Valid"
        step1FailResult.body.errors.size() == 3
        step1FailResult.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1FailResult.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
        step1FailResult.body.errors.get("email")[0] == "Email ya está registrado"
        step1FailResult.body.statusCode == 422

        and: "request with response is saved"
        affiliateRequestRepository.count() == 2
        with(affiliateRequestRepository.findOne(Entities.request.clientId.isNull().and(Entities.request.requestType.eq("step1V1")))) {
            requestType == "step1V1"
            clientId == null
            applicationId == null
            request != null
            response != null
        }

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)

        then:
        application.isPresent()
        application.get().getSourceType() == LoanApplicationSourceType.AFFILIATE
        !application.get().getSourceName().isEmpty()

        and:
        !clientApiHelper.getClientInfo().authenticated

        when:
        def status = restTemplate.exchange("/api/affiliate/v1/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == "PhoneVerification"

        when:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v1/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        step2Result.statusCodeValue == 200
        step2Result.body.message == "SMS Code Success"
        step2Result.body.applicationUuid == step1Result.body.applicationUuid
        step2Result.body.token

        and: "request with response is saved"
        affiliateRequestRepository.count() == 3
        with(affiliateRequestRepository.findOne(Entities.request.clientId.isNotNull().and(Entities.request.requestType.eq("step2")))) {
            requestType == "step2"
            clientId == savedRequest.clientId
            applicationId == savedRequest.applicationId
            request != null
            response != null
        }

        when:
        def clientInfo = clientApiHelper.getClientInfo(step2Result.body.token)

        then:
        with(clientInfo) {
            authenticated
            id
            state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_IOVATION_BLACK_BOX
            temporaryPassword
            !qualifiedForNewLoan
        }

        and:
        with(clientService.get(clientInfo.id)) {
            number
            documentNumber == client.affiliateRegistrationStep1FormV1.documentNumber
            firstName == client.affiliateRegistrationStep1FormV1.firstName
            lastName == client.affiliateRegistrationStep1FormV1.lastName
            !secondLastName
            phone == client.affiliateRegistrationStep1FormV1.mobilePhone
            acceptTerms
            acceptMarketing
        }

        when:
        iovationService.saveBlackbox(new SaveBlackboxCommand(clientId: clientInfo.id, blackBox: "blackBox", ipAddress: "12.23.44.22"))
        status = restTemplate.exchange("/api/affiliate/v1/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == "DocumentForm"

        when: "can't verify twice"
        def secondStep2Result = restTemplate.postForEntity("/api/affiliate/v1/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        secondStep2Result.statusCodeValue == 422
        secondStep2Result.body.message == "SMS Code Failure"
        secondStep2Result.body.applicationUuid == step1Result.body.applicationUuid
        !secondStep2Result.body.token


        and: "request with response is saved"
        affiliateRequestRepository.count() == 4

        when:
        client.clientId = application.get().clientId
        TestFirstLoanAffiliateWorkflow firstLoanWorkflow = TestFactory.firstLoanAffiliateWorkflow(client, application.get().workflowId)
        firstLoanWorkflow.runAll()
        status = restTemplate.exchange("/api/affiliate/v2/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == COMPLETED.toString()

        when:
        def report = affiliateService.findLeadReportByClientId(client.clientId)

        then:
        report.isPresent()
        report.get().clientId == client.clientId
        !report.get().repeatedClient
    }

    def "registration V2 - no blackbox"() {
        given:
        def client = TestFactory.newClient()
        def form = client.affiliateRegistrationStep1Form.setBlackbox(null)

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()
        !step1Result.body.repeated
        step1Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1

        when: "if same request than failed"
        def step1FailResult = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1FailResult.statusCodeValue == 200
        step1FailResult.body.repeated
        !step1FailResult.body.applicationUuid
        !step1FailResult.body.token
        step1FailResult.body.applicationStatus == FAILED.toString()

        and: "request and response is saved"
        affiliateRequestRepository.count() == 2

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)

        then:
        application.isPresent()
        application.get().getSourceType() == LoanApplicationSourceType.AFFILIATE
        !application.get().getSourceName().isEmpty()

        and:
        !clientApiHelper.getClientInfo().authenticated

        when:
        def status = restTemplate.exchange("/api/affiliate/v2/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == PENDING.toString()

        when:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v2/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        step2Result.statusCodeValue == 200
        step2Result.body.message == "SMS Code Success"
        step2Result.body.applicationUuid == step1Result.body.applicationUuid
        step2Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 3

        when:
        def clientInfo = clientApiHelper.getClientInfo(step2Result.body.token)

        then:
        with(clientInfo) {
            authenticated
            id
            state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_IOVATION_BLACK_BOX
            temporaryPassword
            !qualifiedForNewLoan
        }

        and:
        with(clientService.get(clientInfo.id)) {
            number
            documentNumber == form.documentNumber
            firstName == form.firstName
            lastName == form.lastName
            !secondLastName
            phone == form.mobilePhone
            acceptTerms
            acceptMarketing
        }

        when:
        iovationService.saveBlackbox(new SaveBlackboxCommand(clientId: clientInfo.id, blackBox: "blackBox", ipAddress: "12.23.44.22"))
        status = restTemplate.exchange("/api/affiliate/v2/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == ACCEPTED.toString()

        when: "can't verify twice"
        def secondStep2Result = restTemplate.postForEntity("/api/affiliate/v2/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        secondStep2Result.statusCodeValue == 422
        secondStep2Result.body.message == "SMS Code Failure"
        secondStep2Result.body.applicationUuid == step1Result.body.applicationUuid
        !secondStep2Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 4

        when:
        client.clientId = application.get().clientId
        TestFirstLoanAffiliateWorkflow firstLoanWorkflow = TestFactory.firstLoanAffiliateWorkflow(client, application.get().workflowId)
        firstLoanWorkflow.runAll()
        status = restTemplate.exchange("/api/affiliate/v2/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == COMPLETED.toString()

        when:
        def report = affiliateService.findLeadReportByClientId(client.clientId)

        then:
        report.isPresent()
        report.get().clientId == client.clientId
        !report.get().repeatedClient
    }

    def "status of not existing application V1"() {
        when:
        def status = restTemplate.exchange("/api/affiliate/v1/status/12345", HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        !status.body.status
    }

    def "status of not existing application V2"() {
        when:
        def status = restTemplate.exchange("/api/affiliate/v2/status/12345", HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == NOT_FOUND.toString()
    }

    def "registration with phone number already registered V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.statusCode == 422

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1
    }

    def "registration new affiliate V1 - experian decline request"() {
        def client = TestFactory.newClient()
        affiliateRequestRepository.count() == 0
        mockExperianCaisProvider.throwError = true

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.applicationUuid
        step1Result.body.applicationStatusDetails == "Rejected by Experian"
        !step1Result.body.repeated
        !step1Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1

        cleanup:
        mockExperianCaisProvider.throwError = false
    }

    def "registration new affiliate V1 - equifax decline request"() {
        def client = TestFactory.newClient()
        affiliateRequestRepository.count() == 0
        mockEquifaxProvider.throwError = true

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.applicationUuid
        step1Result.body.applicationStatusDetails == "Rejected by Equifax"
        !step1Result.body.repeated
        !step1Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1

        cleanup:
        mockEquifaxProvider.throwError = false
    }

    def "registration with phone number already registered V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.statusCode == 422

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1
    }

    def "registration new affiliate V2 - experian decline request"() {
        given:
        def client = TestFactory.newClient()
        mockExperianCaisProvider.throwError = true

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        clientRepository.findAll().size() == 0
        step1Result.statusCodeValue == 200
        !step1Result.body.applicationUuid
        step1Result.body.applicationStatus == DECLINED.name()
        step1Result.body.applicationStatusDetails == 'Rejected by Experian'
        !step1Result.body.repeated
        !step1Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1

        cleanup:
        mockExperianCaisProvider.throwError = false
    }

    def "registration new affiliate V2 - equifax decline request"() {
        given:
        def client = TestFactory.newClient()
        mockEquifaxProvider.throwError = true

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        clientRepository.findAll().size() == 0
        step1Result.statusCodeValue == 200
        !step1Result.body.applicationUuid
        step1Result.body.applicationStatus == DECLINED.name()
        step1Result.body.applicationStatusDetails == 'Rejected by Equifax'
        !step1Result.body.repeated
        !step1Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1

        cleanup:
        mockEquifaxProvider.throwError = false
    }

    def "registration with phone number already registered, but application form not completed V1"() {
        given:
        def client = TestFactory.newClient().signUp().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.statusCode == 422

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1
    }

    def "registration with phone number already registered, but application form not completed V2"() {
        given:
        def client = TestFactory.newClient().signUp().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.statusCode == 422

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1
    }

    def "registration with dni already registered V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
        step1Result.body.statusCode == 422
    }

    def "registration with dni already registered V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
        step1Result.body.statusCode == 422
    }

    def "registration with email already registered V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.email = client.email

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.statusCode == 422
    }

    def "registration with email already registered V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.statusCode == 422
    }

    def "registration with email already registered, but application form not completed  V1"() {
        given:
        def client = TestFactory.newClient().signUp().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.email = client.email

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.statusCode == 422
    }

    def "registration with email already registered, but application form not completed  V2"() {
        given:
        def client = TestFactory.newClient().signUp().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.statusCode == 422
    }

    def "step 1 validation V1"() {
        given:
        def client = TestFactory.newClient()

        when:
        modifier(client.affiliateRegistrationStep1FormV1)

        and:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1 && step1Result.body.errors.get(field).size() > 0
        step1Result.body.statusCode == 422

        where:
        field           | modifier
        "principal"     | { it -> it.amount = null }
        "term"          | { it -> it.term = null }
        "name"          | { it -> it.firstName = null }
        "name"          | { it -> it.firstName = "" }
        "surname"       | { it -> it.lastName = null }
        "surname"       | { it -> it.lastName = "" }
        "id_doc_number" | { it -> it.documentNumber = null }
        "id_doc_number" | { it -> it.documentNumber = "" }
        "id_doc_number" | { it -> it.documentNumber = "INVALID" }
        "id_doc_number" | { it -> it.documentNumber = TestFactory.newClient().registerDirectly().signUpForm.documentNumber }
        "gender"        | { it -> it.gender = null }
        "gender"        | { it -> it.gender = "" }
        "gender"        | { it -> it.gender = "INVALID" }
        "birth_date"    | { it -> it.dateOfBirth = null }
        "street"        | { it -> it.street = null }
        "street"        | { it -> it.street = "" }
        "zipcode"       | { it -> it.postalCode = null }
        "zipcode"       | { it -> it.postalCode = "" }
        "city"          | { it -> it.city = null }
        "city"          | { it -> it.city = "" }
        "phone"         | { it -> it.mobilePhone = null }
        "phone"         | { it -> it.mobilePhone = "" }
        "phone"         | { it -> it.mobilePhone = TestFactory.newClient().registerDirectly().signUpForm.mobilePhone }
        "email"         | { it -> it.email = null }
        "email"         | { it -> it.email = "" }
        "email"         | { it -> it.email = "INVALID" }
        "email"         | { it -> it.email = "INVALID@INVALID" }
        "email"         | { it -> it.email = "INVALID@INVALID@INVALID" }
        "email"         | { it -> it.email = "INVALID@.INVALID" }
        "email"         | { it -> it.email = "INVALID.INVALID" }
        "email"         | { it -> it.email = ".INVALID" }
        "email"         | { it -> it.email = TestFactory.newClient().registerDirectly().signUpForm.email }
        "tos"           | { it -> it.tos = null }
        "tos"           | { it -> it.tos = 0 }
    }

    def "step 1 validation V2"() {
        given:
        def client = TestFactory.newClient()

        when:
        modifier(client.affiliateRegistrationStep1Form)

        and:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1 && step1Result.body.errors.get(field).size() > 0
        step1Result.body.statusCode == 422

        where:
        field           | modifier
        "principal"     | { it -> it.amount = null }
        "term"          | { it -> it.term = null }
        "name"          | { it -> it.firstName = null }
        "name"          | { it -> it.firstName = "" }
        "surname"       | { it -> it.lastName = null }
        "surname"       | { it -> it.lastName = "" }
        "id_doc_number" | { it -> it.documentNumber = null }
        "id_doc_number" | { it -> it.documentNumber = "" }
        "id_doc_number" | { it -> it.documentNumber = "INVALID" }
        "id_doc_number" | { it -> it.documentNumber = TestFactory.newClient().registerDirectly().signUpForm.documentNumber }
        "gender"        | { it -> it.gender = null }
        "gender"        | { it -> it.gender = "" }
        "gender"        | { it -> it.gender = "INVALID" }
        "birth_date"    | { it -> it.dateOfBirth = null }
        "street"        | { it -> it.street = null }
        "street"        | { it -> it.street = "" }
        "zipcode"       | { it -> it.postalCode = null }
        "zipcode"       | { it -> it.postalCode = "" }
        "city"          | { it -> it.city = null }
        "city"          | { it -> it.city = "" }
        "phone"         | { it -> it.mobilePhone = null }
        "phone"         | { it -> it.mobilePhone = "" }
        "phone"         | { it -> it.mobilePhone = TestFactory.newClient().registerDirectly().signUpForm.mobilePhone }
        "email"         | { it -> it.email = null }
        "email"         | { it -> it.email = "" }
        "email"         | { it -> it.email = "INVALID" }
        "email"         | { it -> it.email = "INVALID@INVALID" }
        "email"         | { it -> it.email = "INVALID@INVALID@INVALID" }
        "email"         | { it -> it.email = "INVALID@.INVALID" }
        "email"         | { it -> it.email = "INVALID.INVALID" }
        "email"         | { it -> it.email = ".INVALID" }
        "email"         | { it -> it.email = TestFactory.newClient().registerDirectly().signUpForm.email }
        "tos"           | { it -> it.tos = 0 }
    }

    def "step 1 dni blacklist validation V1"() {
        given:
        def client = TestFactory.newClient()

        when:
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_DNI, value1: client.dni))

        and:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1 && step1Result.body.errors.get("id_doc_number").size() > 0
        step1Result.body.statusCode == 422
    }

    def "step 1 rejected loan validation V1"() {
        given:
        def client = TestFactory.newClient().signUp()
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_PHONE, value1: client.mobilePhone))
        def workflow = client.toLoanWorkflow().runAll()

        and:
        workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.REJECTED

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.statusCode == 422
    }


    def "step 1 dni blacklist validation V2"() {
        given:
        def client = TestFactory.newClient()

        when:
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_DNI, value1: client.dni))

        and:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 1 && step1Result.body.errors.get("id_doc_number").size() > 0
        step1Result.body.statusCode == 422
    }

    def "step 1 rejected loan validation v2"() {
        given:
        def client = TestFactory.newClient().signUp()
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_PHONE, value1: client.mobilePhone))
        def workflow = client.toLoanWorkflow().runAll()

        and:
        workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.REJECTED

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.statusCode == 422
    }

    def "step 2 validation V1"() {
        given:
        def client = TestFactory.newClient()

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.applicationUuid
        !step1Result.body.repeated
        !step1Result.body.token

        when:
        def affiliateRegistrationStep2Form = new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)

        and:
        modifier(affiliateRegistrationStep2Form)

        and:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v1/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, affiliateRegistrationStep2Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step2Result.statusCodeValue == 422
        step2Result.body.message == "Form Affiliate Not Valid"
        step2Result.body.errors.size() == 1 && step2Result.body.errors.get(field).size() > 0
        step2Result.body.statusCode == 422

        where:
        field        | modifier
        "code"       | { it -> it.code = null }
        "code"       | { it -> it.code = "" }
        "request_id" | { it -> it.applicationUuid = null }
        "request_id" | { it -> it.applicationUuid = "" }
        "tos"        | { it -> it.tos = null }
        "tos"        | { it -> it.tos = 0 }
    }

    def "step 2 validation V2"() {
        given:
        def client = TestFactory.newClient()

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()
        !step1Result.body.repeated
        step1Result.body.token

        when:
        def affiliateRegistrationStep2Form = new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)

        and:
        modifier(affiliateRegistrationStep2Form)

        and:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v1/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, affiliateRegistrationStep2Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step2Result.statusCodeValue == 422
        step2Result.body.message == "Form Affiliate Not Valid"
        step2Result.body.errors.size() == 1 && step2Result.body.errors.get(field).size() > 0
        step2Result.body.statusCode == 422

        where:
        field        | modifier
        "code"       | { it -> it.code = null }
        "code"       | { it -> it.code = "" }
        "request_id" | { it -> it.applicationUuid = null }
        "request_id" | { it -> it.applicationUuid = "" }
        "tos"        | { it -> it.tos = null }
        "tos"        | { it -> it.tos = 0 }
    }

    def "registration existing email, phone and dni V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.email = client.email
        client2.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone
        client2.affiliateRegistrationStep1FormV1.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 3
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
    }

    def "repeated client - check analytics data"() {
        when:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()

        then:
        assert webAnalyticsService.findLatest(new WebAnalyticsEventQuery()
            .setClientId(client.getClientId())
            .setEventTypes([AlfaConstants.WEB_ANALYTICS_SIGN_UP_EVENT]))
            .isPresent()

        when:
        def repeater = TestFactory.newClient()
        repeater.affiliateRegistrationStep1Form.email = client.email
        repeater.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone
        repeater.affiliateRegistrationStep1Form.documentNumber = client.dni

        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, repeater.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.repeated

        and:
        assert webAnalyticsService.findLatest(new WebAnalyticsEventQuery()
            .setClientId(client.getClientId())
            .setEventTypes([AlfaConstants.WEB_ANALYTICS_LOAN_APPLICATION_EVENT]))
            .isPresent()
    }

    def "registration existing email, phone and dni V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone
        client2.affiliateRegistrationStep1Form.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.token
        step1Result.body.applicationStatus == PENDING.toString()

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)

        then:
        application.isPresent()
        application.get().status == LoanApplicationStatus.OPEN
        application.get().statusDetail == LoanApplicationStatusDetail.PENDING

        when:
        def affiliateRegistrationStep2Form = new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)

        and:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v2/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, affiliateRegistrationStep2Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        step2Result.statusCodeValue == 200
        step2Result.body.message == "SMS Code Success"
        step2Result.body.applicationUuid == step1Result.body.applicationUuid
        step2Result.body.token

        when:
        def report = affiliateService.findLeadReportByClientId(client.clientId)

        then:
        report.isPresent()
        report.get().clientId == client.clientId
        report.get().repeatedClient
    }

    def "registration existing email, phone and dni different clients V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client3 = TestFactory.newClient()
        client3.affiliateRegistrationStep1FormV1.email = client.email
        client3.affiliateRegistrationStep1FormV1.mobilePhone = client2.mobilePhone
        client3.affiliateRegistrationStep1FormV1.documentNumber = client2.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client3.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 3
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
    }

    def "registration existing email, phone and dni different clients V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client3 = TestFactory.newClient()
        client3.affiliateRegistrationStep1Form.email = client.email
        client3.affiliateRegistrationStep1Form.mobilePhone = client2.mobilePhone
        client3.affiliateRegistrationStep1Form.documentNumber = client2.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client3.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 3
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
    }

    def "registration existing email and phone, dni different clients V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client3 = TestFactory.newClient()
        client3.affiliateRegistrationStep1FormV1.email = client.email
        client3.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone
        client3.affiliateRegistrationStep1FormV1.documentNumber = client2.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client3.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 3
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
    }

    def "registration existing email and phone, dni different clients V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client3 = TestFactory.newClient()
        client3.affiliateRegistrationStep1Form.email = client.email
        client3.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone
        client3.affiliateRegistrationStep1Form.documentNumber = client2.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client3.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.token
        step1Result.body.applicationStatus == PENDING.toString()

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)

        then:
        application.isPresent()
        application.get().status == LoanApplicationStatus.OPEN
        application.get().statusDetail == LoanApplicationStatusDetail.PENDING

        when:
        def affiliateRegistrationStep2Form = new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)

        and:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v2/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, affiliateRegistrationStep2Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        step2Result.statusCodeValue == 200
        step2Result.body.message == "SMS Code Success"
        step2Result.body.applicationUuid == step1Result.body.applicationUuid
        step2Result.body.token

        when:
        def report = affiliateService.findLeadReportByClientId(client.clientId)

        then:
        report.isPresent()
        report.get().clientId == client.clientId
        report.get().repeatedClient
    }

    def "registration existing email and phone V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.email = client.email
        client2.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
    }

    def "registration existing email and phone V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.token
        step1Result.body.applicationStatus == PENDING.toString()

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)

        then:
        application.isPresent()
        application.get().status == LoanApplicationStatus.OPEN
        application.get().statusDetail == LoanApplicationStatusDetail.PENDING

        when:
        def affiliateRegistrationStep2Form = new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)

        and:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v2/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, affiliateRegistrationStep2Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        step2Result.statusCodeValue == 200
        step2Result.body.message == "SMS Code Success"
        step2Result.body.applicationUuid == step1Result.body.applicationUuid
        step2Result.body.token

        when:
        def report = affiliateService.findLeadReportByClientId(client.clientId)

        then:
        report.isPresent()
        report.get().clientId == client.clientId
        report.get().repeatedClient
    }

    def "registration existing email and phone, but application form not completed V1"() {
        given:
        def client = TestFactory.newClient().signUp().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.email = client.email
        client2.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
    }

    def "registration existing email and phone, but application form not completed V2"() {
        given:
        def client = TestFactory.newClient().signUp().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.token
        step1Result.body.applicationStatus == PENDING.toString()

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)

        then:
        application.isPresent()
        application.get().status == LoanApplicationStatus.OPEN
        application.get().statusDetail == LoanApplicationStatusDetail.PENDING

        when:
        def workflow = workflowService.getWorkflow(application.get().workflowId)

        then:
        workflow.currentActivity.isPresent()
        workflow.currentActivity.get().name == UnderwritingWorkflows.Activities.APPLICATION_FORM

        when:
        def report = affiliateService.findLeadReportByClientId(client.clientId)

        then:
        report.isPresent()
        report.get().clientId == client.clientId
        report.get().repeatedClient
    }

    def "registration existing email and phone different clients V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client3 = TestFactory.newClient()
        client3.affiliateRegistrationStep1FormV1.email = client.email
        client3.affiliateRegistrationStep1FormV1.mobilePhone = client2.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client3.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
    }

    def "registration existing email and phone different clients V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client3 = TestFactory.newClient()
        client3.affiliateRegistrationStep1Form.email = client.email
        client3.affiliateRegistrationStep1Form.mobilePhone = client2.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client3.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
    }

    def "registration existing email and dni V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.email = client.email
        client2.affiliateRegistrationStep1FormV1.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
    }

    def "registration existing email and dni V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email
        client2.affiliateRegistrationStep1Form.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
    }

    def "registration existing phone and dni V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone
        client2.affiliateRegistrationStep1FormV1.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
    }

    def "registration existing phone and dni V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().cancelActiveApplication()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone
        client2.affiliateRegistrationStep1Form.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("id_doc_number")[0] == "DNI/NIE ya está registrado"
    }

    def "registration existing user with active loan V1"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(200.0, 30, date("2019-01-01"))
            .toClient()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.email = client.email
        client2.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
    }

    def "registration existing user with active loan V2"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(200.0, 30, date("2019-01-01"))
            .toClient()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.repeated
        !step1Result.body.applicationUuid
        !step1Result.body.token
        step1Result.body.applicationStatus == FAILED.toString()

        when:
        def applications = loanApplicationService.find(LoanApplicationQuery.byClientId(client.clientId))

        then:
        applications.size() == 1
        with(applications[0]) {
            status == LoanApplicationStatus.CLOSED
            statusDetail == LoanApplicationStatusDetail.APPROVED
            !sourceName
            sourceType == LoanApplicationSourceType.ORGANIC
        }
    }

    def "registration existing user with active loan application V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1FormV1.email = client.email
        client2.affiliateRegistrationStep1FormV1.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.common.AffiliateApiError.class)

        then:
        step1Result.statusCodeValue == 422
        step1Result.body.message == "Form Affiliate Not Valid"
        step1Result.body.errors.size() == 2
        step1Result.body.errors.get("phone")[0] == "Teléfono ya está registrado"
        step1Result.body.errors.get("email")[0] == "Email ya está registrado"
    }

    def "registration existing user with active loan application V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.repeated
        !step1Result.body.applicationUuid
        !step1Result.body.token
        step1Result.body.applicationStatus == FAILED.toString()

        when:
        def applications = loanApplicationService.find(LoanApplicationQuery.byClientId(client.clientId))

        then:
        applications.size() == 1
        with(applications[0]) {
            status == LoanApplicationStatus.OPEN
            statusDetail == LoanApplicationStatusDetail.PENDING
            !sourceName
            sourceType == LoanApplicationSourceType.ORGANIC
        }
    }

    @Unroll
    def "accept marketing is accepted in step1/step2, both optional and step2 overrides it"() {
        given:
        def client = TestFactory.newClient()

        client.affiliateRegistrationStep1Form.acceptMarketing = step1Marketing

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200

        when:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v2/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1, acceptMarketing: step2Marketing)), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        and:
        def clientInfo = clientApiHelper.getClientInfo(step2Result.body.token)

        then:
        with(clientService.get(clientInfo.id)) {
            acceptMarketing == expectedMarketing
        }

        where:
        step1Marketing | step2Marketing | expectedMarketing
        null           | null           | false
        null           | true           | true
        null           | false          | false
        true           | true           | true
        true           | false          | false
        false          | true           | true
        false          | false          | false
        false          | null           | false
        true           | null           | true
    }

    def "registration with email of soft deleted client V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().softDelete()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid

        and:
        loanApplicationService.findByUuid(step1Result.body.applicationUuid).get().clientId != client.clientId
    }

    def "registration with phone of soft deleted client V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().softDelete()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid

        and:
        loanApplicationService.findByUuid(step1Result.body.applicationUuid).get().clientId != client.clientId
    }

    def "registration with dni of soft deleted client V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().softDelete()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid

        and:
        loanApplicationService.findByUuid(step1Result.body.applicationUuid).get().clientId != client.clientId
    }

    def "registration with email, phone and dni of soft deleted client V1"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().softDelete()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone
        client2.affiliateRegistrationStep1Form.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid

        and:
        loanApplicationService.findByUuid(step1Result.body.applicationUuid).get().clientId != client.clientId
    }

    def "registration with email of soft deleted client V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().softDelete()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()

        and:
        loanApplicationService.findByUuid(step1Result.body.applicationUuid).get().clientId != client.clientId
    }

    def "registration with phone of soft deleted client V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().softDelete()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()

        and:
        loanApplicationService.findByUuid(step1Result.body.applicationUuid).get().clientId != client.clientId
    }

    def "registration with dni of soft deleted client V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().softDelete()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()

        and:
        loanApplicationService.findByUuid(step1Result.body.applicationUuid).get().clientId != client.clientId
    }

    def "registration with email, phone and dni of soft deleted client V2"() {
        given:
        def client = TestFactory.newClient().signUp().saveApplicationForm().softDelete()
        def client2 = TestFactory.newClient()
        client2.affiliateRegistrationStep1Form.email = client.email
        client2.affiliateRegistrationStep1Form.mobilePhone = client.mobilePhone
        client2.affiliateRegistrationStep1Form.documentNumber = client.dni

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client2.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()

        and:
        loanApplicationService.findByUuid(step1Result.body.applicationUuid).get().clientId != client.clientId
    }

    @Unroll
    def "V1 step1 validation - optional fields"() {
        given:
        def client = TestFactory.newClient()

        when:
        modifier(client.affiliateRegistrationStep1Form)

        and:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v1/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1FormV1), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated

        where:
        modifier                               | _
        { it -> it.maritalStatus = null }      | _
        { it -> it.numberOfDependants = null } | _
        { it -> it.education = null }          | _
        { it -> it.workSector = null }         | _
        { it -> it.occupation = null }         | _
        { it -> it.employedSince = null }      | _
        { it -> it.nextSalaryDate = null }     | _
        { it -> it.housingTenure = null }      | _
        { it -> it.excludedFromASNEF = null }  | _
        { it -> it.monthlyExpenses = null }    | _
        { it -> it.netoIncome = null }         | _
        { it -> it.incomeSource = null }       | _
    }

    @Unroll
    def "V2 step1 validation - optional fields"() {
        given:
        def client = TestFactory.newClient()

        when:
        modifier(client.affiliateRegistrationStep1Form)

        and:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        !step1Result.body.repeated
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()

        where:
        modifier                               | _
        { it -> it.maritalStatus = null }      | _
        { it -> it.numberOfDependants = null } | _
        { it -> it.education = null }          | _
        { it -> it.workSector = null }         | _
        { it -> it.occupation = null }         | _
        { it -> it.employedSince = null }      | _
        { it -> it.nextSalaryDate = null }     | _
        { it -> it.housingTenure = null }      | _
        { it -> it.excludedFromASNEF = null }  | _
        { it -> it.monthlyExpenses = null }    | _
        { it -> it.netoIncome = null }         | _
        { it -> it.incomeSource = null }       | _
    }

    def "registration V2 - with blackbox"() {
        given:
        def client = TestFactory.newClient()
        def form = client.affiliateRegistrationStep1Form

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()
        !step1Result.body.repeated
        step1Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 1

        when: "if same request than failed"
        def step1FailResult = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1FailResult.statusCodeValue == 200
        step1FailResult.body.repeated
        !step1FailResult.body.applicationUuid
        !step1FailResult.body.token
        step1FailResult.body.applicationStatus == FAILED.toString()

        and: "request and response is saved"
        affiliateRequestRepository.count() == 2

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid).get()

        then:
        application.getSourceType() == LoanApplicationSourceType.AFFILIATE
        !application.getSourceName().isEmpty()

        and:
        !clientApiHelper.getClientInfo().authenticated

        when:
        client.clientId = application.clientId
        def status = restTemplate.exchange("/api/affiliate/v2/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == PENDING.toString()

        when:
        def step2Result = restTemplate.postForEntity("/api/affiliate/v2/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        step2Result.statusCodeValue == 200
        step2Result.body.message == "SMS Code Success"
        step2Result.body.applicationUuid == step1Result.body.applicationUuid
        step2Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 3

        when:
        def clientInfo = clientApiHelper.getClientInfo(step2Result.body.token)

        then:
        with(clientInfo) {
            authenticated
            id
            state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_INSTANTOR
            temporaryPassword
            !qualifiedForNewLoan
        }

        and:
        iovationService.findLatestBlackBox(new IovationBlackboxQuery(clientId: client.clientId)).present

        and:
        with(clientService.get(clientInfo.id)) {
            number
            documentNumber == form.documentNumber
            firstName == form.firstName
            lastName == form.lastName
            !secondLastName
            phone == form.mobilePhone
            acceptTerms
            acceptMarketing
        }

        when:
        status = restTemplate.exchange("/api/affiliate/v2/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == ACCEPTED.toString()

        when: "can't verify twice"
        def secondStep2Result = restTemplate.postForEntity("/api/affiliate/v2/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        secondStep2Result.statusCodeValue == 422
        secondStep2Result.body.message == "SMS Code Failure"
        secondStep2Result.body.applicationUuid == step1Result.body.applicationUuid
        !secondStep2Result.body.token

        and: "request and response is saved"
        affiliateRequestRepository.count() == 4

        when:
        TestFirstLoanAffiliateWorkflow firstLoanWorkflow = TestFactory.firstLoanAffiliateWorkflow(client, application.workflowId)
        firstLoanWorkflow.runAll()
        status = restTemplate.exchange("/api/affiliate/v2/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == COMPLETED.toString()

        when:
        def report = affiliateService.findLeadReportByClientId(client.clientId)

        then:
        report.isPresent()
        report.get().clientId == client.clientId
        !report.get().repeatedClient
    }

    def "Affiliate registration V2 - skip blackbox UI screen if present in step1 request"() {
        given:
        def client = TestFactory.newClient()

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, client.affiliateRegistrationStep1Form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()
        !step1Result.body.repeated
        step1Result.body.token

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)
        client.clientId = application.get().clientId
        TestFirstLoanAffiliateWorkflow firstLoanWorkflow = TestFactory.firstLoanAffiliateWorkflow(client, application.get().workflowId)
        firstLoanWorkflow.runBeforeActivity(DOCUMENT_FORM)
        def workflow = firstLoanWorkflow.getWorkflow()

        then:
        with(workflow.activities.find { it.name == IOVATION_BLACKBOX_RUN_1 }) {
            it.status == ActivityStatus.COMPLETED
            it.resolution == Resolutions.SKIP
        }
    }

    def "Affiliate registration V2 - show blackbox UI screen if not present in step1 request"() {
        given:
        def client = TestFactory.newClient()
        def form = client.affiliateRegistrationStep1Form.setBlackbox(null);

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()
        !step1Result.body.repeated
        step1Result.body.token

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)
        client.clientId = application.get().clientId
        TestFirstLoanAffiliateWorkflow firstLoanWorkflow = TestFactory.firstLoanAffiliateWorkflow(client, application.get().workflowId)
        firstLoanWorkflow.runBeforeActivity(IOVATION_BLACKBOX_RUN_1)

        then:
        firstLoanWorkflow.getActivity(IOVATION_BLACKBOX_RUN_1).isActive()

        when:
        iovationService.saveBlackbox(new SaveBlackboxCommand(clientId: client.getClientId(), blackBox: "blackBox", ipAddress: "12.23.44.22"))

        then:
        firstLoanWorkflow.getActivityStatus(IOVATION_BLACKBOX_RUN_1) == ActivityStatus.COMPLETED
    }

    def "Affiliate registration V2 - activities experian and equifax are autocompleted"() {
        given:
        def client = TestFactory.newClient()
        def form = client.affiliateRegistrationStep1Form.setBlackbox(null)

        when:
        def step1Result = restTemplate.postForEntity("/api/affiliate/v2/step1", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, form), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep1Result.class)

        then:
        step1Result.statusCodeValue == 200
        step1Result.body.applicationUuid
        step1Result.body.applicationStatus == PENDING.toString()
        !step1Result.body.repeated
        step1Result.body.token

        when:
        def application = loanApplicationService.findByUuid(step1Result.body.applicationUuid)

        then:
        application.isPresent()
        application.get().getSourceType() == LoanApplicationSourceType.AFFILIATE
        !application.get().getSourceName().isEmpty()

        and:
        !clientApiHelper.getClientInfo().authenticated

        when:
        def status = restTemplate.exchange("/api/affiliate/v2/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)

        then:
        status.statusCodeValue == 200
        status.body.status == PENDING.toString()

        when:
        client.clientId = application.get().clientId
        TestFirstLoanAffiliateWorkflow firstLoanWorkflow = TestFactory.firstLoanAffiliateWorkflow(client, application.get().workflowId)

        then:
        def workflow = firstLoanWorkflow.getWorkflow()
        with(workflow.activities.find { it.name == EXPERIAN_CAIS_RESUMEN_RUN_1 }) {
            it.status == ActivityStatus.WAITING
            workflow.hasAttribute(Attributes.EXPERIAN_CAIS_RESUMEN_RESPONSE_ID)
            workflow.hasAttribute(Attributes.EXPERIAN_CAIS_OPERACIONES_RESPONSE_ID)
            workflow.hasAttribute(Attributes.EQUIFAX_RESPONSE_ID)
        }

        when:
        def savedRequest = affiliateRequestRepository.findOne(Entities.request.clientId.isNotNull())
        with(savedRequest) {
            requestType == "step1"
            clientId != null
            applicationId != null
            request != null
            response != null
        }
        def step2Result = restTemplate.postForEntity("/api/affiliate/v1/step2", ApiHelper.authorized(TEST_AFFILIATE_API_KEY, new AffiliateRegistrationStep2Form(code: "0", applicationUuid: step1Result.body.applicationUuid, tos: 1)), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.AffiliateRegistrationStep2Result.class)

        then:
        step2Result.statusCodeValue == 200
        step2Result.body.message == "SMS Code Success"
        step2Result.body.applicationUuid == step1Result.body.applicationUuid
        step2Result.body.token

        and: "request with response is saved"
        with(affiliateRequestRepository.findOne(Entities.request.clientId.isNotNull().and(Entities.request.requestType.eq("step2")))) {
            requestType == "step2"
            clientId == savedRequest.clientId
            applicationId == savedRequest.applicationId
            request != null
            response != null
        }

        when:
        def clientInfo = clientApiHelper.getClientInfo(step2Result.body.token)

        then:
        with(clientInfo) {
            authenticated
            id
            state == fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_IOVATION_BLACK_BOX
            temporaryPassword
            !qualifiedForNewLoan
        }

        and:
        with(clientService.get(clientInfo.id)) {
            number
            documentNumber == form.documentNumber
            firstName == form.firstName
            lastName == form.lastName
            !secondLastName
            phone == form.mobilePhone
            acceptTerms
            acceptMarketing
        }

        when:
        iovationService.saveBlackbox(new SaveBlackboxCommand(clientId: clientInfo.id, blackBox: "blackBox", ipAddress: "12.23.44.22"))
        status = restTemplate.exchange("/api/affiliate/v1/status/" + step1Result.body.applicationUuid, HttpMethod.GET, ApiHelper.authorized(TEST_AFFILIATE_API_KEY), fintech.spain.alfa.web.controllers.web.AffiliateRegistrationApi.StatusResult.class)
        workflow = firstLoanWorkflow.getWorkflow()

        then:
        status.statusCodeValue == 200
        status.body.status == "DocumentForm"

        and:
        with(workflow.activities.find { it.name == EXPERIAN_CAIS_RESUMEN_RUN_1 }) {
            it.status == ActivityStatus.COMPLETED
            it.resolution == "OK"
            it.resolutionDetail == "AutoCompleted"
        }
        and:
        with(workflow.activities.find { it.name == EXPERIAN_CAIS_OPERACIONES_RUN_1 }) {
            it.status == ActivityStatus.COMPLETED
            it.resolution == "OK"
            it.resolutionDetail == "AutoCompleted"
        }
        and:
        with(workflow.activities.find { it.name == EQUIFAX_RUN_1 }) {
            it.status == ActivityStatus.COMPLETED
            it.resolution == "OK"
            it.resolutionDetail == "AutoCompleted"
        }

    }
}
