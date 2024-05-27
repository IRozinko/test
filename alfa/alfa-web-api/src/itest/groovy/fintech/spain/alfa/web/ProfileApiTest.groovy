package fintech.spain.alfa.web

import com.google.common.collect.ImmutableMap
import fintech.JsonUtils
import fintech.TimeMachine
import fintech.crm.attachments.AttachmentSubType
import fintech.lending.core.application.LoanApplicationQuery
import fintech.lending.core.application.LoanApplicationSourceType
import fintech.lending.core.creditlimit.AddCreditLimitCommand
import fintech.lending.core.creditlimit.CreditLimitService
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.settings.SettingsService
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.web.models.AcceptMarketingRequest
import fintech.spain.alfa.web.models.AcceptMarketingResponse
import fintech.spain.alfa.web.models.ApproveUpsellOfferRequest
import fintech.spain.alfa.web.models.ExtensionStatus
import fintech.spain.alfa.web.models.LoanApplicationData
import fintech.spain.alfa.web.models.LoansResponse
import fintech.spain.alfa.web.models.PersonalDetailsResponse
import fintech.spain.alfa.web.models.PrepareOfferRequest
import fintech.spain.alfa.web.models.SubmitLoanApplicationRequest
import fintech.spain.alfa.product.lending.Offer
import fintech.spain.alfa.product.lending.OfferSettings
import fintech.spain.alfa.product.lending.UnderwritingFacade
import fintech.spain.alfa.product.registration.forms.AffiliateData
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.spain.alfa.product.testing.TestFactory

import fintech.spain.alfa.product.web.WebAuthorities
import fintech.spain.alfa.product.workflow.common.Resolutions
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows
import fintech.spain.web.common.ApiError
import fintech.web.api.models.OkResponse
import fintech.workflow.Activity
import fintech.workflow.WorkflowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod

import java.nio.file.Files
import java.nio.file.Path

import static fintech.DateUtils.date

class ProfileApiTest extends AbstractAlfaApiTest {

    @Autowired
    DcTestCases dcTestCases

    @Autowired
    CreditLimitService creditLimitService

    @Autowired
    SettingsService settingsService

    @Autowired
    WorkflowService workflowService

    def "no loans"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/web/profile/loans", HttpMethod.GET, ApiHelper.authorized(token, ""), LoansResponse.class)

        then:
        assert result.statusCodeValue == 200
        assert result.body.loans.isEmpty()
    }

    def "paid loan"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/web/profile/loans", HttpMethod.GET, ApiHelper.authorized(token, ""), LoansResponse.class)

        then:
        assert result.statusCodeValue == 200
        assert result.body.loans.size() == 1
        with(result.body.loans[0]) {
            assert principalDisbursed == 300.00
            assert interestDue == 0.00
            assert totalDue == 0.000
            assert status == LoanStatus.CLOSED
            assert statusDetail == LoanStatusDetail.PAID
            assert maturityDate == date("2018-01-01").plusDays(30)
            assert closeDate == date("2018-02-01")
            assert number
            assert extensionOptions.isEmpty()
            assert installments.size() == 1
            // no agreement attachment as loan not issued via workflow
        }
    }

    def "Only loans available for WEB_PAYMENT_ONLY_AUTHORITY"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = clientApiHelper.getClientToken(client.clientId, WebAuthorities.WEB_PAYMENT_ONLY)

        when:
        def result = restTemplate.exchange("/api/web/profile/loans", HttpMethod.GET, ApiHelper.authorized(token, ""), LoansResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.loans.isEmpty()

        when:
        def prepareOfferResult = restTemplate.exchange("/api/web/profile/prepare-offer", HttpMethod.POST, ApiHelper.authorized(token, ""), Offer.class)

        then:
        prepareOfferResult.statusCode.is4xxClientError()
    }

    def "accept marketing change flag"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = clientApiHelper.getClientToken(client.clientId, WebAuthorities.WEB_FULL)

        when:
        def result = restTemplate.postForEntity("/api/web/profile/accept-marketing", ApiHelper.authorized(token, new AcceptMarketingRequest().setAcceptMarketing(true)), AcceptMarketingResponse.class)

        then:
        assert result.body.acceptMarketing

        and:
        assert client.getClient().isAcceptMarketing()

        when:
        result = restTemplate.postForEntity("/api/web/profile/accept-marketing", ApiHelper.authorized(token, new AcceptMarketingRequest().setAcceptMarketing(false)), AcceptMarketingResponse.class)

        then:
        assert !result.body.acceptMarketing

        and:
        assert !client.getClient().isAcceptMarketing()
    }

    def "Getting loans sorted asc by issue date"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def secondLoanByIssueDate = client.issueActiveLoan(300.00, 30, date("2018-02-07")).repayAll(date("2018-03-07"))
        def firstLoanByIssueDate = client.issueActiveLoan(300.00, 30, date("2018-01-07")).repayAll(date("2018-02-06"))
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/web/profile/loans", HttpMethod.GET, ApiHelper.authorized(token, ""), LoansResponse.class)

        then:
        assert result.statusCodeValue == 200
        assert result.body.loans.size() == 2
        assert result.body.loans[0].number == firstLoanByIssueDate.loan.number
        assert result.body.loans[1].number == secondLoanByIssueDate.loan.number
    }

    def "open loan issued via workflow"() {
        def client = TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toClient()
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/web/profile/loans", HttpMethod.GET, ApiHelper.authorized(token, ""), LoansResponse.class)

        then:
        assert result.statusCodeValue == 200
        assert result.body.loans.size() == 1
        with(result.body.loans[0]) {
            assert principalDisbursed == client.signUpForm.amount
            assert interestDue == 35.00
            assert totalDue == client.signUpForm.amount + 35.00
            assert statusDetail == LoanStatusDetail.ACTIVE
            assert maturityDate
            assert !closeDate
            assert !extensionOptions.isEmpty()
            assert loanAgreementAttachment.fileId
            assert loanAgreementAttachment.name
            assert standardInformationAttachment.fileId
            assert standardInformationAttachment.name
            assert extensionOptions.each {
                !it.getDiscountPct()
                !it.getDiscountPrice()
            }
            assert extensionStatus == ExtensionStatus.AVAILABLE
        }

        when: "download agreement"
        def downloadResult = restTemplate.exchange("/api/web/files/" + result.body.loans[0].loanAgreementAttachment.fileId, HttpMethod.GET, ApiHelper.authorized(token, ""), byte[].class)

        then:
        assert downloadResult.statusCodeValue == 200
        assert downloadResult.body.length > 0
    }

    def "Upsell available"() {
        given:
        def client = TestFactory.newClient()
            .setDateOfBirth(TimeMachine.today().minusYears(36))
            .setAmount(1000.00)
            .signUp()

        client
            .withCreditLimit(960.00)
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())
            .toClient()

        2.times {
            client
                .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
                .toLoanWorkflow()
                .runAll()
                .exportDisbursement()
                .toLoan()
                .repayAll(TimeMachine.today())
        }

        client
            .submitApplicationAndStartFirstLoanWorkflow(150.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAfterActivity(UnderwritingWorkflows.Activities.LOAN_OFFER_EMAIL)

        when:
        def token = apiHelper.login(client)

        and:
        def result = restTemplate.exchange("/api/web/profile/loan-application", HttpMethod.GET, ApiHelper.authorized(token, ""), LoanApplicationData.class)

        then:
        result.statusCodeValue == 200

        with(result.body) {
            upsellOffers.size() == 5
            nominalApr > 0

            with(upsellOffers[0]) {
                principal == 200.00
                nominalApr > 0
            }

            with(upsellOffers[1]) {
                principal == 300.00
                nominalApr > 0
            }

            with(upsellOffers[2]) {
                principal == 400.00
                nominalApr > 0
            }

            with(upsellOffers[3]) {
                principal == 500.00
                nominalApr > 0
            }

            with(upsellOffers[4]) {
                principal == 600.00
                nominalApr > 0
            }
        }
        def newPrincipal = result.body.upsellOffers[4].principal

        when:
        result = restTemplate.postForEntity("/api/web/profile/approve-upsell-offer/${client.toLoanWorkflow().applicationId}", ApiHelper.authorized(token, new ApproveUpsellOfferRequest().setAbsource("VARIANT").setPrincipal(newPrincipal)), OkResponse.class)

        then:
        assert result.statusCodeValue == 200

        and:
        applicationService.get(client.applicationId).offeredPrincipal == newPrincipal
        client.toLoanWorkflow().getAttribute(UnderwritingFacade.UPSELL_AB_TEST_WORKFLOW_ATTRIBUTE).get() == "VARIANT"

    }

    def "offer"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)

        when:
        def settingsResult = restTemplate.exchange("/api/web/profile/offer-settings", HttpMethod.GET, ApiHelper.authorized(token, ""), OfferSettings.class)

        then:
        assert settingsResult.statusCodeValue == 200
        assert settingsResult.body.maxAmount > 0.00
        assert settingsResult.body.interestDiscountPercent >= 0

        when:
        def offerResult = restTemplate.postForEntity("/api/web/profile/prepare-offer", ApiHelper.authorized(token, new PrepareOfferRequest().setAmount(300.00).setTermInDays(30)), Offer.class)

        then:
        assert offerResult.statusCodeValue == 200
        assert offerResult.body.principal == 300.0
        assert offerResult.body.nominalApr > 0.0
    }


    def "offer - not passes validation for pre-offer request amount and term "() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)

        when:
        def settingsResult = restTemplate.exchange("/api/web/profile/offer-settings", HttpMethod.GET, ApiHelper.authorized(token, ""), OfferSettings.class)

        then:
        assert settingsResult.statusCodeValue == 200
        assert settingsResult.body.maxAmount > 0.00
        assert settingsResult.body.interestDiscountPercent >= 0

        when:
        def offerResult = restTemplate.postForEntity("/api/web/profile/prepare-offer", ApiHelper.authorized(token, new PrepareOfferRequest().setAmount(0.00).setTermInDays(0)), ApiError.class)

        then:
        assert offerResult.statusCodeValue == 400
    }

    def "offer max principal from credit_limit entry rounded up"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(200.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)
        def creditLimit = new AddCreditLimitCommand()
        creditLimit.clientId = client.clientId
        creditLimit.limit = 338.00
        creditLimit.activeFrom = date("2018-01-01")
        creditLimit.reason = "Test"

        when:
        creditLimitService.addLimit(creditLimit)
        def settingsResult = restTemplate.exchange("/api/web/profile/offer-settings", HttpMethod.GET, ApiHelper.authorized(token, ""), OfferSettings.class)

        then:
        settingsResult.statusCodeValue == 200
        settingsResult.body.maxAmount == 340.00
        settingsResult.body.interestDiscountPercent >= 0
    }

    def "offer max principal from credit_limit entry rounded down"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(200.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)
        def creditLimit = new AddCreditLimitCommand()
        creditLimit.clientId = client.clientId
        creditLimit.limit = 324.00
        creditLimit.activeFrom = date("2018-01-01")
        creditLimit.reason = "Test"

        when:
        creditLimitService.addLimit(creditLimit)
        def settingsResult = restTemplate.exchange("/api/web/profile/offer-settings", HttpMethod.GET, ApiHelper.authorized(token, ""), OfferSettings.class)

        then:
        settingsResult.statusCodeValue == 200
        settingsResult.body.maxAmount == 320.00
        settingsResult.body.interestDiscountPercent >= 0
    }

    def "offer default max principal if credit_limit missed"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
        def token = apiHelper.login(client)

        when:
        def settingsResult = restTemplate.exchange("/api/web/profile/offer-settings", HttpMethod.GET, ApiHelper.authorized(token, ""), OfferSettings.class)

        then:
        settingsResult.statusCodeValue == 200
        settingsResult.body.maxAmount == 300.00
        settingsResult.body.interestDiscountPercent >= 0
    }

    def "submit loan application - application source is saved as affiliate"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)

        expect:
        assert client.findOpenLoans().size() == 0

        when:
        restTemplate.postForEntity("/api/web/profile/submit-loan-application", ApiHelper.authorized(token, new SubmitLoanApplicationRequest()
            .setAmount(300.00)
            .setTermInDays(30)
            .setAffiliate(new AffiliateData().setAffiliateName("AffiliateName"))
        ), Object.class)

        then:
        def application = applicationService.findLatest(LoanApplicationQuery.byClientId(client.clientId))
        assert application.isPresent()
        assert application.get().getSourceType() == LoanApplicationSourceType.AFFILIATE
        assert application.get().getSourceName() == "AffiliateName"
    }

    def "submit loan application"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)

        expect:
        assert client.findOpenLoans().size() == 0

        when:
        def result = restTemplate.postForEntity("/api/web/profile/submit-loan-application", ApiHelper.authorized(token, new SubmitLoanApplicationRequest().setAmount(300.00).setTermInDays(30)), Object.class)

        then:
        assert result.statusCodeValue == 200

        and:
        assert client.toLoanWorkflow().isActive()

        when:
        client.toLoanWorkflow().runAll().exportDisbursement()

        then:
        assert client.findOpenLoans().size() == 1
    }


    def "submit loan application - not passes validation for too short period"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)

        expect:
        assert client.findOpenLoans().size() == 0

        when:
        def result = restTemplate.postForEntity("/api/web/profile/submit-loan-application", ApiHelper.authorized(token, new SubmitLoanApplicationRequest().setAmount(300.00).setTermInDays(5)), ApiError.class)

        then:
        result.statusCodeValue == 400
        result.body.fieldErrors.containsKey("termInDays")
    }

    def "submit loan application - not passes validation for too long period"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)

        expect:
        assert client.findOpenLoans().size() == 0

        when:
        def result = restTemplate.postForEntity("/api/web/profile/submit-loan-application", ApiHelper.authorized(token, new SubmitLoanApplicationRequest().setAmount(300.00).setTermInDays(31)), ApiError.class)

        then:
        result.statusCodeValue == 400
        result.body.fieldErrors.containsKey("termInDays")
    }

    def "Profile details - logged client can see info"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/web/profile/personal-details", HttpMethod.GET, ApiHelper.authorized(token, ""), PersonalDetailsResponse.class)

        then:
        result.statusCodeValue == 200
        result.body.email == client.email
        result.body.firstName == client.firstName
        result.body.lastName == client.lastName
        assert result.body.address
    }

    def "Profile details - not logged client can not see info"() {
        given:
        def token = "invalid"

        when:
        def result = restTemplate.exchange("/api/web/profile/personal-details", HttpMethod.GET, ApiHelper.authorized(token, ""), PersonalDetailsResponse.class)

        then:
        result.statusCodeValue == 403
    }

    def "Uploaded documents are seen in the list of attachments"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def token = apiHelper.login(client)
        def file = getUserFileResource()
        when:
        def uploadResult = restTemplate.postForEntity("/api/web/profile/upload-file", ApiHelper.multipart(token, ImmutableMap.of("file", file, "type", AttachmentSubType.DNI_NIE_ANVERSO.name())), OkResponse.class)
        def filesResult = restTemplate.exchange("/api/web/profile/uploaded-files", HttpMethod.GET, ApiHelper.authorized(token, ""), Object.class)

        then:
        uploadResult.statusCodeValue == 200
        filesResult.body.size() == 1
        filesResult.body[0].fileName == file.getFilename()
        filesResult.body[0].subType == AttachmentSubType.DNI_NIE_ANVERSO.name()
    }

    def "Uploading documents not allowed for unregistered client"() {
        given:
        def file = getUserFileResource()
        when:
        def uploadResult = restTemplate.postForEntity("/api/web/profile/upload-file", ApiHelper.multipart(null, ImmutableMap.of("file", file)), OkResponse.class)
        then:
        uploadResult.statusCodeValue == 403
    }

    def "Uploaded documents triggers workflow to next step"() {
        given:
        def documentSettings = settingsService.getJson(AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, AlfaSettings.IdDocumentValiditySettings.class)
        documentSettings.setRequestIdUploadForFirstLoan(true)
        documentSettings.setRequestIdUploadForSecondAndLaterLoan(true)
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, textValue: JsonUtils.writeValueAsString(documentSettings)))
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.ENABLE_DNI_UPLOADING, booleanValue: true))
        def client = TestFactory.newClient()

        when:
        client.randomEmailAndName("First Loan Workflow Completed")
            .signUp()
            .toLoanWorkflow()
            .run(UnderwritingWorkflows.Activities.DNI_DOC_UPLOAD, null)
        def testWorkflow = apiHelper.findWorkflow(client)
        Optional<Activity> activity = testWorkflow.flatMap(this.&activeState)

        then:
        activity.isPresent()
        activity.get().name == UnderwritingWorkflows.Activities.DNI_DOC_UPLOAD

        when:
        def token = apiHelper.login(client)
        def result = restTemplate.postForEntity("/api/web/profile/uploaded-files", ApiHelper.authorized(token, ""), OkResponse.class)
        sleep(100)
        testWorkflow = apiHelper.findWorkflow(client)
        activity = testWorkflow.flatMap(this.&activeState)

        then:
        result.statusCodeValue == 200
        activity.isPresent()
        activity.get().name == UnderwritingWorkflows.Activities.INSTANTOR_MANUAL_CHECK

        client.toLoanWorkflow().getActivity(UnderwritingWorkflows.Activities.DNI_DOC_UPLOAD).getResolution() == Resolutions.OK
    }

    def "Rescheduled loan's new contract is available for download "() {
        def loan = dcTestCases.rescheduled(2)
        def client = loan.toClient()
        def token = apiHelper.login(client)

        when:
        def result = restTemplate.exchange("/api/web/profile/loans", HttpMethod.GET, ApiHelper.authorized(token, ""), LoansResponse.class)

        then:
        assert result.statusCodeValue == 200
        assert result.body.loans.size() == 1

        when: "download rescheduled contract"
        def downloadResult = restTemplate.exchange("/api/web/files/" + result.body.loans[0].reschedulingAgreementAttachment.fileId, HttpMethod.GET, ApiHelper.authorized(token, ""), byte[].class)

        then:
        assert downloadResult.statusCodeValue == 200
        assert downloadResult.body.length > 0
    }

    def "Submit loan application with Promo Code"() {
        given:
        def client = TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .repayAll(date("2018-02-01"))
            .toClient()
        def token = apiHelper.login(client)

        expect:
        client.findOpenLoans().size() == 0

        when:
        def result = restTemplate.postForEntity("/api/web/profile/submit-loan-application",
            ApiHelper.authorized(token, new SubmitLoanApplicationRequest().setAmount(300.00).setTermInDays(30).setPromoCode("LOOP-2019")), Object.class)

        then:
        result.statusCodeValue == 200

        and:
        client.toLoanWorkflow().isActive()

        when:
        client.toLoanWorkflow().runAll().exportDisbursement()

        then:
        client.findOpenLoans().size() == 1

    }

    private static Resource getUserFileResource() throws IOException {
        Path tempFile = Files.createTempFile("test-upload", ".doc")
        File file = tempFile.toFile()
        return new FileSystemResource(file)
    }
}
