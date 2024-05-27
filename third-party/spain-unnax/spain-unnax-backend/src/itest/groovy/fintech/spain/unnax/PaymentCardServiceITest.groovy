package fintech.spain.unnax

import fintech.JsonUtils
import fintech.spain.unnax.callback.model.CallbackRequest
import fintech.spain.unnax.callback.model.CreditCardPreAuthCallbackData
import fintech.spain.unnax.db.CreditCardEntity
import fintech.spain.unnax.db.CreditCardRepository
import fintech.spain.unnax.db.CreditCardStatus
import fintech.spain.unnax.event.CreditCardPreAuthorizeEvent
import fintech.spain.unnax.impl.PaymentCardServiceImpl
import fintech.spain.unnax.model.CreditCardQuery
import fintech.spain.unnax.webhook.model.CreditCardState
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import javax.transaction.Transactional
import java.time.LocalDateTime

@Transactional
class PaymentCardServiceITest extends AbstractBaseSpecification {

    @Subject
    @Autowired
    PaymentCardServiceImpl paymentCardService

    @Autowired
    CreditCardRepository creditCardRepository


    def "payment card queries"() {
        given:
        creditCardRepository.save(new CreditCardEntity(
            clientNumber: "C2352346578",
            callbackTransactionId: "4837529034562034975620937465nv237058",
            active: true,
            cardToken: "675239457620349756209346729348",
            cardExpireYear: 22,
            cardExpireMonth: 2,
            cardHolderName: "John Snow",
            cardBrand: "VISA",
            cardBank: "Axia bank",
            orderCode: "C2352346578.3256792348",
            errorDetails: null,
            status: CreditCardStatus.PROCESSED,
            automaticPaymentEnabled: true,
            pan: "2524356",
            bin: 2345642L
        ))

        when:
        def card = paymentCardService.findCreditCard(CreditCardQuery.byOrderCode("C2352346578.3256792348"))
        then:
        card.isPresent()
        with(card.get()) {
            clientNumber == "C2352346578"
            callbackTransactionId == "4837529034562034975620937465nv237058"
            active
            cardToken == "675239457620349756209346729348"
            cardExpireYear == 22
            cardExpireMonth == 2
            cardHolderName == "John Snow"
            cardBrand == "VISA"
            cardBank == "Axia bank"
            orderCode == "C2352346578.3256792348"
            errorDetails == null
            status == CreditCardStatus.PROCESSED
            automaticPaymentEnabled
            pan == "2524356"
            bin == 2345642L
        }
    }

    def "Manage automatic payments for client"() {
        given:
        creditCardRepository.save(new CreditCardEntity(
            clientNumber: "C2352346578",
            callbackTransactionId: "4837529034562034975620937465nv237058",
            active: true,
            cardToken: "675239457620349756209346729348",
            cardExpireYear: 22,
            cardExpireMonth: 2,
            cardHolderName: "John Snow",
            cardBrand: "VISA",
            cardBank: "Axia bank",
            orderCode: "C2352346578.3256792348",
            errorDetails: null,
            status: CreditCardStatus.PROCESSED,
            automaticPaymentEnabled: false,
            pan: "2524356",
            bin: 2345642L
        ))

        when:
        paymentCardService.enableAutomaticPayments("C2352346578")

        then:
        def testCase1 = paymentCardService.findCreditCard(CreditCardQuery.byOrderCode("C2352346578.3256792348"))
        testCase1.isPresent()
        with(testCase1.get()) {
            clientNumber == "C2352346578"
            automaticPaymentEnabled == Boolean.TRUE
        }

        when:
        paymentCardService.disableAutomaticPayments("C2352346578")

        then:
        def testCase2 = paymentCardService.findCreditCard(CreditCardQuery.byOrderCode("C2352346578.3256792348"))
        testCase2.isPresent()
        with(testCase2.get()) {
            clientNumber == "C2352346578"
            automaticPaymentEnabled == Boolean.FALSE
        }

    }

    def "Handle credit card authorization"() {
        given:
        CallbackRequest callbackRequest = new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")
            .setData(JsonUtils.readTree(new CreditCardPreAuthCallbackData()
                .setState(CreditCardState.SUCCESS.value)
                .setDate(LocalDateTime.now().toString())
                .setExpirationDate("2021-05-22")
                .setExpireMonth("5")
                .setExpireYear("2021")
                .setCardBank("1111222233334444")
                .setCardHolder("Gold")
                .setOrderCode("order_code")
                .setToken("123456-78654-34216-1870")
                .setPan("3349")
                .setAmount(100)))

        when:
        paymentCardService.handleCreditCardPreAuthorized(new CreditCardPreAuthorizeEvent(callbackRequest))

        then:
        paymentCardService.findCreditCard(CreditCardQuery.byOrderCode("order_code")).isPresent()
    }

}
