package fintech.spain.alfa.web

import fintech.TimeMachine
import fintech.crm.client.ClientService
import fintech.lending.core.application.LoanApplicationQuery
import fintech.lending.core.application.LoanApplicationSourceType
import fintech.spain.alfa.product.registration.forms.DocumentNumberForm
import fintech.spain.alfa.product.testing.RandomData
import fintech.spain.alfa.product.testing.TestClient
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.web.common.ApiError
import fintech.web.api.models.OkResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import spock.lang.Unroll

import static fintech.BigDecimalUtils.amount
import static fintech.spain.alfa.product.AlfaConstants.CLIENT_ATTRIBUTE_INCOME_SOURCE
import static ApiHelper.authorized

class RegistrationApiTest extends AbstractAlfaApiTest {

    @Autowired
    ClientService clientService

    @Autowired
    ClientApiHelper clientApiHelper

    @Unroll
    def "sign up fails with invalid #fieldError"() {
        given:
        def form = TestFactory.newClient().buildSignUpForm()

        when:
        modifier(form)
        def result = restTemplate.postForEntity("/api/public/web/registration/signup", form, ApiError.class)

        then:
        result.statusCodeValue == 400
        result.body.fieldErrors[fieldError].message == "Campo obligatorio"

        where:
        fieldError                 | modifier
        "firstName"                | { it -> it.setFirstName("") }
        "firstName"                | { it -> it.setFirstName(null) }
        "lastName"                 | { it -> it.setLastName("") }
        "lastName"                 | { it -> it.setLastName(null) }
        "email"                    | { it -> it.setEmail("") }
        "email"                    | { it -> it.setEmail(null) }
        "mobilePhone"              | { it -> it.setMobilePhone("") }
        "mobilePhone"              | { it -> it.setMobilePhone(null) }
        "password"                 | { it -> it.setPassword("") }
        "password"                 | { it -> it.setPassword(null) }
        "documentNumber"           | { it -> it.setDocumentNumber("") }
        "documentNumber"           | { it -> it.setDocumentNumber(null) }
        "countryCodeOfNationality" | { it -> it.setCountryCodeOfNationality("") }
        "countryCodeOfNationality" | { it -> it.setCountryCodeOfNationality(null) }
        "amount"                   | { it -> it.setAmount(null) }
        "termInDays"               | { it -> it.setTermInDays(null) }
        "acceptTerms"              | { it -> it.setAcceptTerms(false) }
        "acceptVerification"       | { it -> it.setAcceptVerification(false) }
        "blackbox"                 | { it -> it.setBlackbox(null) }
    }

    def "Save application form: income source is required"() {
        given:
        def client = TestFactory.newClient()
        def token = client.signUpWithoutApplication().token()
        def form = client.applicationForm
            .setIncomeSource("")

        when:
        def result = restTemplate.postForEntity("/api/web/registration/save-application-form",
            authorized(token, form), ApiError.class)

        then:
        result.statusCodeValue == 400
        result.body.fieldErrors["incomeSource"].message == "Campo obligatorio"
    }

    def "Save application form: income source saved successfully"() {
        given:
        def client = TestFactory.newClient()
        def token = client.signUpWithoutApplication().token()
        def form = client.applicationForm

        when:
        def key = CLIENT_ATTRIBUTE_INCOME_SOURCE
        def result = restTemplate.postForEntity("/api/web/registration/save-application-form",
            authorized(token, form), OkResponse.class)

        then:
        result.statusCodeValue == 200
        with(client.client.attributes) {
            it.containsKey(key)
            it.get(key) == form.incomeSource
        }
    }

    def "phone number is normalized &  saved"() {
        given:
        def form = TestFactory.newClient().setMobilePhone(" +34 63 4343 434__ ").buildSignUpForm()

        when:
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        assert clientService.get(signUpResult.body.clientId).phone == "634343434"
    }

    def "application source is saved as affiliate"() {
        given:
        def form = TestFactory.newClient().buildAffiliateSignUpForm()
        form.countryCodeOfNationality = 'ES'

        when:
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        def application = applicationService.findLatest(LoanApplicationQuery.byClientId(signUpResult.body.clientId))
        assert application.isPresent()
        assert application.get().getSourceType() == LoanApplicationSourceType.AFFILIATE
        assert !application.get().getSourceName().isEmpty()
    }

    def "change and verify phone"() {
        when:
        def form = TestFactory.newClient().buildSignUpForm()
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        assert signUpResult.statusCodeValue == 200

        when:
        def newPhoneNumber = RandomData.randomPhoneNumber()
        def result = restTemplate.postForEntity("/api/web/registration/change-phone", authorized(signUpResult.body.token, new fintech.spain.alfa.web.models.ChangePhoneRequest().setMobilePhone(newPhoneNumber)), Object.class)

        then:
        result.statusCodeValue == 200

        when:
        result = restTemplate.postForEntity("/api/web/registration/verify-phone", authorized(signUpResult.body.token, new fintech.spain.alfa.web.models.VerifyPhoneRequest().setCode("0")), Object.class)

        then:
        result.statusCodeValue == 200

        and:
        clientService.get(signUpResult.body.clientId).phone == newPhoneNumber
    }

    def 'cannot verify a phone which is already verified'() {
        when:
        def form = TestFactory.newClient().buildSignUpForm()
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        assert signUpResult.statusCodeValue == 200

        when:
        restTemplate.postForEntity("/api/web/registration/send-phone-verification-code", authorized(signUpResult.body.token), Object.class)
        def result = restTemplate.postForEntity("/api/web/registration/verify-phone", authorized(signUpResult.body.token, new fintech.spain.alfa.web.models.VerifyPhoneRequest().setCode("0")), Object.class)

        then:
        result.statusCodeValue == 200

        when:
        result = restTemplate.postForEntity("/api/web/registration/verify-phone", authorized(signUpResult.body.token, new fintech.spain.alfa.web.models.VerifyPhoneRequest().setCode("0")), Object.class)

        then:
        assert result.statusCodeValue == 400
        assert result.body.fieldErrors["code"].code == 'AlreadyVerified'
    }

    def 'save document number if already has it'() {
        given:
        def client = TestFactory.newClient().signUp()

        when:
        def req = new DocumentNumberForm(client.dni, client.countryCodeOfNationality)

        and:
        def result = restTemplate.postForEntity("/api/web/registration/save-document-number", ApiHelper.authorized(client.token(), req), Object.class)

        then:
        result.statusCodeValue == 400
    }

    def 'save document number incorrect dni'() {
        given:
        def client = TestFactory.newClient().signUp()
        client.deleteIdentityDocuments()

        when:
        def req = new DocumentNumberForm("12312312G", client.countryCodeOfNationality)

        and:
        def result = restTemplate.postForEntity("/api/web/registration/save-document-number", ApiHelper.authorized(client.token(), req), ApiError.class)

        then:
        result.statusCodeValue == 400
        result.body.fieldErrors["documentNumber"].message == "DNI/NIE incorrecto"
    }

    def 'save document number if client doesnt have'() {
        given:
        def client = TestFactory.newClient().signUp()
        client.deleteIdentityDocuments()

        when:
        def req = new DocumentNumberForm(client.dni, client.countryCodeOfNationality)

        and:
        def result = restTemplate.postForEntity("/api/web/registration/save-document-number", ApiHelper.authorized(client.token(), req), Object.class)

        then:
        result.statusCodeValue == 200

        and:
        client.getClient().documentNumber == client.dni
    }

    def 'save document number when a soft deleted client has same document number'() {
        given:
        TestClient clientDeleted = TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
            .softDelete()

        when:
        def client = TestFactory.newClient().signUp()
        client.deleteIdentityDocuments()
        def req = new DocumentNumberForm(clientDeleted.signUpForm.documentNumber, client.countryCodeOfNationality)
        def result = restTemplate.postForEntity("/api/web/registration/save-document-number", ApiHelper.authorized(client.token(), req), Object.class)

        then:
        result.statusCodeValue == 200

        and:
        client.getClient().documentNumber == clientDeleted.signUpForm.documentNumber
        client.clientId != clientDeleted.clientId
    }

    def 'throws error on incorrect verification code'() {
        when:
        def form = TestFactory.newClient().buildSignUpForm()
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        assert signUpResult.statusCodeValue == 200

        when:
        restTemplate.postForEntity("/api/web/registration/send-phone-verification-code", authorized(signUpResult.body.token), Object.class)
        def result = restTemplate.postForEntity("/api/web/registration/verify-phone", authorized(signUpResult.body.token, new fintech.spain.alfa.web.models.VerifyPhoneRequest().setCode("1")), Object.class)

        then:
        assert result.statusCodeValue == 400
        assert result.body.fieldErrors["code"].code == 'InvalidValue'
    }

    def 'client not qualified for new verification code after limit exceeded'() {
        when:
        def form = TestFactory.newClient().buildSignUpForm()
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)
        def auth = authorized(signUpResult.body.token)
        def result = repeatSendingVerificationCodeUntilLimitIsExceeded(auth)

        then:
        assert result.nextAttemptInSeconds > 0
        assert result.availableAttempts == 0

        when:
        result = restTemplate.postForEntity("/api/web/registration/send-phone-verification-code", auth, fintech.spain.alfa.web.models.SendVerificationCodeOkResult.class).body

        then:
        assert !result.codeSent
        assert result.nextAttemptInSeconds > 0
        assert result.availableAttempts == 0

        when:
        TimeMachine.useFixedClockAt(TimeMachine.now().plusMinutes(20))
        result = restTemplate.postForEntity("/api/web/registration/send-phone-verification-code", auth, fintech.spain.alfa.web.models.SendVerificationCodeOkResult.class).body

        then:
        assert result.codeSent
        assert !result.nextAttemptInSeconds
        assert result.availableAttempts == 4
    }

    @Unroll
    def 'invalid email'() {
        given:
        def form = TestFactory.newClient().buildSignUpForm()

        when:
        form.email = wrongEmail
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, ApiError.class)

        then:
        signUpResult.statusCodeValue == 400
        signUpResult.body.fieldErrors["email"].message == "Correo electrónico invalido"

        where:
        wrongEmail << ["abc", "abc@a", "abc@abc", "abc@abc@abc", ".com", "abc.com", "abc@.com"]
    }

    @Unroll
    def "invalid document number"() {
        given:
        def form = TestFactory.newClient().buildSignUpForm()

        when:
        form.documentNumber = wrongDni
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, ApiError.class)

        then:
        signUpResult.statusCodeValue == 400
        signUpResult.body.fieldErrors["documentNumber"].message == "DNI/NIE incorrecto"

        where:
        wrongDni << ["123456789", "ABCDEFGHI", "1234567A", "123456789A", "A12345678", "X1234567", "X123456789"]
    }

    def "invalid nationality"() {
        given:
        def form = TestFactory.newClient().buildSignUpForm()

        when:
        form.countryCodeOfNationality = "test"
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, ApiError.class)

        then:
        signUpResult.statusCodeValue == 400
        signUpResult.body.fieldErrors["countryCodeOfNationality"].message == "Nacionalidad incorrecta"
    }

    def "client registering with dni but nationality is not spain"() {
        given:
        def form = TestFactory.newClient().buildSignUpForm()

        when:
        form.countryCodeOfNationality = "it"
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, ApiError.class)

        then:
        signUpResult.statusCodeValue == 400
        signUpResult.body.globalErrors[0].code == "ValidDocumentNationality"
        signUpResult.body.globalErrors[0].message == "DNI/NIE no corresponde con nacionalidad"
    }

    def "client registering with nie but nationality is spain"() {
        given:
        def form = TestFactory.newClient().buildSignUpForm()

        when:
        form.documentNumber = "Z3363163Q"
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, ApiError.class)

        then:
        signUpResult.statusCodeValue == 400
        signUpResult.body.globalErrors[0].code == "ValidDocumentNationality"
        signUpResult.body.globalErrors[0].message == "DNI/NIE no corresponde con nacionalidad"
    }

    def "client registering with the email of a soft deleted client"() {
        given:
        TestClient clientDeleted = TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
            .softDelete()

        when:
        def form = TestFactory.newClient().buildSignUpForm()
        form.email = clientDeleted.signUpForm.email
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        signUpResult.statusCodeValue == 200
        signUpResult.body.clientId != clientDeleted.clientId
    }

    def "client registering with the phone of a soft deleted client"() {
        given:
        TestClient clientDeleted = TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
            .softDelete()

        when:
        def form = TestFactory.newClient().buildSignUpForm()
        form.mobilePhone = clientDeleted.signUpForm.mobilePhone
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        signUpResult.statusCodeValue == 200
        signUpResult.body.clientId != clientDeleted.clientId
    }

    def "client registering with the identity document of a soft deleted client"() {
        given:
        TestClient clientDeleted = TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(amount(300), 30L, TimeMachine.today())
            .softDelete()

        when:
        def form = TestFactory.newClient().buildSignUpForm()
        form.documentNumber = clientDeleted.signUpForm.documentNumber
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        signUpResult.statusCodeValue == 200
        signUpResult.body.clientId != clientDeleted.clientId
    }

    def repeatSendingVerificationCodeUntilLimitIsExceeded(HttpEntity auth) {
        def response = null
        (0..4).forEach({
            response = restTemplate.postForEntity("/api/web/registration/send-phone-verification-code", auth, fintech.spain.alfa.web.models.SendVerificationCodeOkResult.class).body
        })
        return response
    }

    def "client registering with promo code"() {
        def client = TestFactory.newClient()

        given:
        def form = client.setPromoCode("SUMMER-MADNESS").buildSignUpForm()

        when:
        def signUpResult = restTemplate.postForEntity("/api/public/web/registration/signup", form, fintech.spain.alfa.web.models.SignUpOkResult.class)

        then:
        with(signUpResult, {
            statusCodeValue == 200
        })
    }
}
