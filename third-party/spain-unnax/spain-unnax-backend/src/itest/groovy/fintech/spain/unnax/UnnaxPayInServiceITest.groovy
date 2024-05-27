package fintech.spain.unnax

import fintech.JsonUtils
import fintech.spain.unnax.callback.model.CallbackRequest
import fintech.spain.unnax.callback.model.PaymentWithCardCallbackData
import fintech.spain.unnax.callback.model.PaymentWithTransferAuthorizedData
import fintech.spain.unnax.callback.model.PaymentWithTransferCompletedData
import fintech.spain.unnax.db.PaymentWithCardRepository
import fintech.spain.unnax.db.PaymentWithTransferAuthorizedRepository
import fintech.spain.unnax.db.PaymentWithTransferCompletedRepository
import fintech.spain.unnax.event.PaymentWithCardEvent
import fintech.spain.unnax.event.PaymentWithTransferAuthorizedEvent
import fintech.spain.unnax.event.PaymentWithTransferCompletedEvent
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Subject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static fintech.spain.unnax.db.Entities.*

class UnnaxPayInServiceITest extends AbstractBaseSpecification {

    @Subject
    @Autowired
    UnnaxPayInService unnaxPayInService

    @Autowired
    PaymentWithCardRepository paymentWithCardRepository

    @Autowired
    PaymentWithTransferAuthorizedRepository paymentWithTransferAuthorizedRepository

    @Autowired
    PaymentWithTransferCompletedRepository paymentWithTransferCompletedRepository

    @Autowired
    ApplicationEventPublisher applicationEventPublisher

    LocalDateTime testTime = LocalDateTime.now()

    def "Handle Card payment event"() {
        given:
        def data = new PaymentWithCardCallbackData()
            .setPan("1111")
            .setBin("00000")
            .setCurrency("EUR")
            .setTransactionType("pay")
            .setExpirationDate("2022")
            .setExpireMonth("2")
            .setExpireYear("22")
            .setCardHolder("Jason Bloom")
            .setCardBrand("VISA")
            .setCardType("DEBIT")
            .setCardCountry("Spain")
            .setCardBank("CiaxaBank")
            .setOrderCode("76734hhg9346593")
            .setToken("asdhfh89ad6g9asffgd6asdfghlasd86")
            .setDate(testTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .setAmount(3567)
            .setConcept("ppp")
            .setState(4)

        def callback = new CallbackRequest()
            .setResponseId("b9f174fc-7763-4853-96f1-ab8bfdbc66a4")
            .setSignature("e8f88f223f7a6269966a74f404fa24ae039302e8")
            .setTriggeredEvent("event_payment_creditcard_pay")
            .setService("payment_creditcard")
            .setEnvironment("unnax_integration_aws")
            .setTraceIdentifier("b9f174fc-7763-4853-96f1-ab8bfdbc66a4")
            .setDate(testTime)
            .setData(JsonUtils.readTree(data))

        when:
        applicationEventPublisher.publishEvent(new PaymentWithCardEvent(callback))

        then:
        def entities = paymentWithCardRepository.findAll(paymentWithCard.orderCode.eq("76734hhg9346593"))
        entities.size() == 1

        with(entities.get(0)) {
            pan == "1111"
            bin == "00000"
            currency == "EUR"
            transactionType == "pay"
            expirationDate == "2022"
            expireMonth == 2
            expireYear == 22
            cardHolder == "Jason Bloom"
            cardBrand == "VISA"
            cardType == "DEBIT"
            cardCountry == "Spain"
            cardBank == "CiaxaBank"
            orderCode == "76734hhg9346593"
            token == "asdhfh89ad6g9asffgd6asdfghlasd86"
            date == testTime
            amount == 35.67
            concept == "ppp"
            state == 4
        }

    }

    def "Handle Payment with transfer auhorized event"() {
        given:
        def data = new PaymentWithTransferAuthorizedData()
            .setOrderCode("CR1556890585")
            .setBankOrderCode("")
            .setAmount(101)
            .setCurrency("EUR")
            .setCustomerCode("")
            .setCustomerNames("")
            .setService("payment_transfer_dbt")
            .setStatus("payment_authorized")
            .setSuccess(true)
            .setErrorMessages("")

        def callback = new CallbackRequest()
            .setResponseId("3e4ae5bb-f70a-4f86-b817-96903a7e1529")
            .setSignature("a4018e82d651ed74c6013fdb470fb68b82ed539b")
            .setDate(testTime)
            .setService("lockstep_sign")
            .setTriggeredEvent("event_payment_transfer_lockstep_authorized")
            .setEnvironment("unnax_integration_aws")
            .setData(JsonUtils.readTree(data))

        when:
        applicationEventPublisher.publishEvent(new PaymentWithTransferAuthorizedEvent(callback))

        then:
        def all = paymentWithTransferAuthorizedRepository.findAll(paymentWithTransferAuthorizedEntity.orderCode.eq("CR1556890585"))
        all.size() == 1

        with(all.get(0)) {
            orderCode == "CR1556890585"
            bankOrderCode == ""
            amount == 1.01
            currency == "EUR"
            customerCode == ""
            customerNames == ""
            service == "payment_transfer_dbt"
            status == "payment_authorized"
            success
            errorMessages == ""
        }

    }

    def "Handle transfer lockstep completed event"() {
        given:
        def data = new PaymentWithTransferCompletedData()
            .setResponseId("0a0566620c114a57a5a22e1682152582")
            .setCustomerCode("")
            .setOrderCode("CR1556890585")
            .setBankOrderCode("")
            .setAmount(10)
            .setDate(testTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .setSuccess(true)
            .setSignature("b5419bb6205dc2918ecdc8009391f74434325507")
            .setResult(true)
            .setAccountNumber("ES7299992018121921123652")
            .setStatus("payment_completed")
            .setService("payment_transfer_dbt")

        def callback = new CallbackRequest()
            .setResponseId("0a0566620c114a57a5a22e1682152582")
            .setSignature("b5419bb6205dc2918ecdc8009391f74434325507")
            .setDate(testTime)
            .setService("payment_transfer_dbt")
            .setTriggeredEvent("event_payment_transfer_lockstep_completed")
            .setEnvironment("unnax_integration_aws")
            .setData(JsonUtils.readTree(data))

        when:
        applicationEventPublisher.publishEvent(new PaymentWithTransferCompletedEvent(callback))

        then:
        def all = paymentWithTransferCompletedRepository.findAll(paymentWithTransferCompletedEntity.orderCode.eq("CR1556890585"))
        all.size() == 1

        with(all.get(0)) {
            customerCode == ""
            orderCode == "CR1556890585"
            bankOrderCode == ""
            amount == 0.1
            date == testTime
            success
            signature == "b5419bb6205dc2918ecdc8009391f74434325507"
            result
            accountNumber == "ES7299992018121921123652"
            status == "payment_completed"
            service == "payment_transfer_dbt"
        }
    }

}
