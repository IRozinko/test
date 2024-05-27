package fintech.payxpert

import fintech.TimeMachine
import fintech.payxpert.impl.MockPayxpertProviderBean
import fintech.payxpert.impl.PayxpertServiceBean

class PayxpertTest extends PayxpertBaseSpecification {

    def "card authorization request"() {
        expect:
        assert service.findPendingPaymentRequests(101L).isEmpty()

        when:
        def request = service.cardAuthorizationRequest(cardAuthorizationRequest())

        then:
        assert request.clientId == 101L
        assert request.amount == 0.01
        assert request.currency == "EUR"
        assert request.status == PaymentRequestStatus.PENDING
        assert request.customerRedirectUrl == "https://payxpert/payment/mock"

        and:
        assert service.findPendingPaymentRequests(101L).size() == 1
    }

    def "check status"() {
        given:
        def request = service.cardAuthorizationRequest(cardAuthorizationRequest())

        when:
        batchJobs.checkPaymentRequestStatuses(TimeMachine.now().plusMinutes(1), 2, 1)

        then:
        with(service.getRequest(request.getId())) {
            assert status == PaymentRequestStatus.SUCCESS
            assert statusCheckAttempts == 1L
        }

        when: "should not be checked again"
        batchJobs.checkPaymentRequestStatuses(TimeMachine.now().plusMinutes(2), 2, 1)

        then:
        with(service.getRequest(request.getId())) {
            assert status == PaymentRequestStatus.SUCCESS
            assert statusCheckAttempts == 1L
        }
    }

    def "check status and expire"() {
        given:
        def request = service.cardAuthorizationRequest(cardAuthorizationRequest())

        when:
        batchJobs.checkPaymentRequestStatuses(TimeMachine.now().plusMinutes(PayxpertServiceBean.PAYMENT_REQUEST_EXPIRES_IN_MINUTES + 1), 2, 1)

        then:
        with(service.getRequest(request.getId())) {
            assert status == PaymentRequestStatus.EXPIRED
            assert statusCheckAttempts == 1L
        }
    }

    def "successful callback"() {
        given:
        def request = service.cardAuthorizationRequest(cardAuthorizationRequest())

        when:
        service.handleCallback(MockPayxpertProviderBean.prepareSuccessCallbackJson(request))

        then:
        with(service.getRequest(request.getId())) {
            assert status == PaymentRequestStatus.SUCCESS
            assert statusCheckAttempts == 0
        }

        when: "should not be checked from batch job"
        batchJobs.checkPaymentRequestStatuses(TimeMachine.now().plusMinutes(2), 2, 1)

        then:
        with(service.getRequest(request.getId())) {
            assert status == PaymentRequestStatus.SUCCESS
            assert statusCheckAttempts == 0
        }
    }

    def "error callback"() {
        given:
        def request = service.cardAuthorizationRequest(cardAuthorizationRequest())

        when:
        service.handleCallback(MockPayxpertProviderBean.prepareErrorCallbackJson(request))

        then:
        with(service.getRequest(request.getId())) {
            assert status == PaymentRequestStatus.ERROR
        }

        when:
        service.handleCallback(MockPayxpertProviderBean.prepareSuccessCallbackJson(request))

        then:
        with(service.getRequest(request.getId())) {
            assert status == PaymentRequestStatus.SUCCESS
        }
    }

    def "card is saved"() {
        given:
        def request = service.cardAuthorizationRequest(cardAuthorizationRequest())

        expect:
        assert !service.findActiveCreditCard(101L).isPresent()

        when:
        service.handleCallback(MockPayxpertProviderBean.prepareSuccessCallbackJson(request))

        then:
        assert service.getRequest(request.getId()).status == PaymentRequestStatus.SUCCESS

        and:
        assert service.findActiveCreditCard(101L).isPresent()
        with(service.findActiveCreditCard(101L).get()) {
            assert clientId == 101L
            assert active
            assert recurringPaymentsEnabled
            assert cardNumber == "411111XXXXXX1111"
            assert cardExpireYear == 2024
            assert cardExpireMonth == 10
            assert cardHolderName == "John Smith"
            assert callbackTransactionId
        }

        when:
        service.removeCreditCard(new RemoveCreditCardCommand()
            .setClientId(101L)
            .setCreditCardId(service.findActiveCreditCard(101L).get().id)
        )

        then:
        assert !service.findActiveCreditCard(101L).isPresent()
    }

    def "rebill"() {
        given:
        def request = service.cardAuthorizationRequest(cardAuthorizationRequest())

        when:
        service.handleCallback(MockPayxpertProviderBean.prepareSuccessCallbackJson(request))
        def card = service.findActiveCreditCard(101L).get()
        def rebill = service.rebill(new RebillCommand()
            .setCreditCardId(card.id)
            .setClientId(101L)
            .setCurrency("EUR")
            .setAmount(99.99)
            .setLoanId(102L)
            .setInvoiceId(103L)
        )

        then:
        assert rebill.status == RebillStatus.SUCCESS
        assert rebill.amount == 99.99
        assert rebill.currency == "EUR"
        assert rebill.clientId == 101L
        assert rebill.loanId == 102L
        assert rebill.invoiceId == 103L
        assert rebill.responseTransactionId
        assert rebill.errorCode == PayxpertConstants.SUCCESS_CODE
        assert rebill.errorMessage
    }

    def "rebill with error"() {
        given:
        def request = service.cardAuthorizationRequest(cardAuthorizationRequest())

        when:
        service.handleCallback(MockPayxpertProviderBean.prepareSuccessCallbackJson(request))
        mockPayxpertProvider.rebillResponse = MockPayxpertProviderBean.failedRebillResponse(PayxpertConstants.ERROR_CODE_DECLINED_BY_ISSUER)
        def card = service.findActiveCreditCard(101L).get()
        def rebill = service.rebill(new RebillCommand()
            .setCreditCardId(card.id)
            .setClientId(101L)
            .setCurrency("EUR")
            .setAmount(99.99)
            .setLoanId(102L)
            .setInvoiceId(103L)
        )

        then:
        assert rebill.status == RebillStatus.ERROR
        assert rebill.amount == 99.99
        assert rebill.currency == "EUR"
        assert rebill.clientId == 101L
        assert rebill.loanId == 102L
        assert rebill.invoiceId == 103L
        assert rebill.responseTransactionId
        assert rebill.errorCode == PayxpertConstants.ERROR_CODE_DECLINED_BY_ISSUER
        assert rebill.errorMessage
    }

    def "get card rebills"() {
        given:
        def request1 = service.cardAuthorizationRequest(cardAuthorizationRequest())
        service.handleCallback(MockPayxpertProviderBean.prepareSuccessCallbackJson(request1))
        def request2 = service.cardAuthorizationRequest(cardAuthorizationRequest(102L))
        service.handleCallback(MockPayxpertProviderBean.prepareSuccessCallbackJson(request2))
        def card = service.findActiveCreditCard(101L).get()
        def card2 = service.findActiveCreditCard(102L).get()

        and:
        mockPayxpertProvider.rebillResponse = MockPayxpertProviderBean.failedRebillResponse(PayxpertConstants.ERROR_CODE_DECLINED_BY_ISSUER)
        def rebill1 = service.rebill(new RebillCommand()
            .setCreditCardId(card.id)
            .setClientId(101L)
            .setCurrency("EUR")
            .setAmount(99.99)
            .setLoanId(102L)
            .setInvoiceId(103L)
        )
        mockPayxpertProvider.rebillResponse = MockPayxpertProviderBean.successRebillResponse()
        def rebill2 = service.rebill(new RebillCommand()
            .setCreditCardId(card.id)
            .setClientId(101L)
            .setCurrency("EUR")
            .setAmount(98.99)
            .setLoanId(105L)
            .setInvoiceId(106L)
        )
        service.rebill(new RebillCommand()
            .setCreditCardId(card2.id)
            .setClientId(102L)
            .setCurrency("EUR")
            .setAmount(99.99)
            .setLoanId(102L)
            .setInvoiceId(103L)
        )

        when:
        def rebills = service.findRebillsByCardId(card.id)

        then:
        rebills.size() == 2
        with(rebills[1]) {
            clientId == rebill1.clientId
            amount == rebill1.amount
            currency == rebill1.currency
            loanId == rebill1.loanId
            invoiceId == rebill1.invoiceId
            status == RebillStatus.ERROR
            errorCode == PayxpertConstants.ERROR_CODE_DECLINED_BY_ISSUER
        }
        with(rebills[0]) {
            clientId == rebill2.clientId
            amount == rebill2.amount
            currency == rebill2.currency
            loanId == rebill2.loanId
            invoiceId == rebill2.invoiceId
            status == RebillStatus.SUCCESS
            errorCode == PayxpertConstants.SUCCESS_CODE
        }
    }

    def "find last payment request"() {
        given:
        def clientId = 101L

        when:
        def request1 = service.cardAuthorizationRequest(cardAuthorizationRequest(clientId))
        def paymentRequest = service.findLastPaymentRequest(clientId)

        then:
        paymentRequest.isPresent()
        paymentRequest.get() == request1

        when:
        def request2 = service.cardAuthorizationRequest(cardAuthorizationRequest(clientId))
        paymentRequest = service.findLastPaymentRequest(clientId)

        then:
        paymentRequest.isPresent()
        paymentRequest.get() == request2
    }

    def "find pending payment requests"() {
        given:
        def clientId = 101L
        def request1 = service.cardAuthorizationRequest(cardAuthorizationRequest(clientId))
        def request2 = service.cardAuthorizationRequest(cardAuthorizationRequest(clientId))
        service.handleCallback(MockPayxpertProviderBean.prepareSuccessCallbackJson(request2))
        def request3 = service.cardAuthorizationRequest(cardAuthorizationRequest(clientId))

        when:
        def requests = service.findPendingPaymentRequests(clientId)

        then:
        requests.size() == 2
        requests[0] == request3
        requests[1] == request1
    }

    private static CardAuthorizationRequestCommand cardAuthorizationRequest(Long clientId = 101L) {
        new CardAuthorizationRequestCommand()
            .setClientId(clientId)
            .setAmount(0.01)
            .setCurrency("EUR")
            .setRedirectUrl("http://server/redirect")
            .setCallbackUrl("http://server/callback")
            .setOrderId(UUID.randomUUID().toString())
    }
}
