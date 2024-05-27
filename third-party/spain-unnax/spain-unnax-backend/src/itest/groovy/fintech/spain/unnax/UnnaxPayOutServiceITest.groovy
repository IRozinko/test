package fintech.spain.unnax

import fintech.JsonUtils
import fintech.spain.unnax.db.TransferAutoStatus
import fintech.spain.unnax.model.TransferAutoQuery
import fintech.spain.unnax.model.UnnaxErrorResponse
import fintech.spain.unnax.transfer.model.TransferAutoRequest
import fintech.spain.unnax.transfer.model.TransferAutoType
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Stepwise
import spock.lang.Subject

import javax.validation.ConstraintViolationException


@Stepwise
class UnnaxPayOutServiceITest extends AbstractBaseSpecification {

    static def IBAN = "NL74ABNA9932384941"

    @Subject
    @Autowired
    UnnaxPayOutService unnaxService

    def "transfer auto"() {
        given:
        def req = new TransferAutoRequest()
            .setAmountInEuros(BigDecimal.TEN)
            .setDestinationAccount(IBAN)
            .setCustomerCode("C123123")
            .setOrderCode(UUID.randomUUID().toString())
            .setCurrency("EUR")
            .setCustomerNames("Jose")
            .setConcept("concept")
            .setTags(Arrays.asList("1", "2"))

        when:
        def resp = unnaxService.transferOut(req)

        then:
        resp.isPresent()
        with(resp.get()) {
            amount == req.getAmount()
        }

        when:
        def transferOut = unnaxService.findRequiredTransferOut(TransferAutoQuery.byOrderCode(req.orderCode))

        then:
        transferOut
        with(transferOut) {
            amount == req.getAmount()
            destinationAccount == IBAN
            customerCode == req.customerCode
            orderCode == req.orderCode
            currency == req.currency
            customerNames == req.customerNames
            concept == req.concept
            tags == '1, 2'
            sourceAccount == req.sourceAccount

            status == TransferAutoStatus.CREATED
            transferType == TransferAutoType.STANDARD
            orderPendingAt
            orderCreatedAt
        }
    }

    def "transfer auto - duplicated bank order id"() {
        given:
        def req1 = new TransferAutoRequest()
            .setAmountInEuros(BigDecimal.TEN)
            .setDestinationAccount(IBAN)
            .setCustomerCode("C123123")
            .setBankOrderCode("bank_order_code")
            .setCustomerNames("Jose")
            .setOrderCode("1111")

        def req2 = new TransferAutoRequest()
            .setAmountInEuros(BigDecimal.TEN)
            .setDestinationAccount(IBAN)
            .setCustomerCode("C123123")
            .setBankOrderCode("bank_order_code")
            .setCustomerNames("Jose")
            .setOrderCode("2222")

        when:
        def response = unnaxService.transferOut(req1)

        then:
        response.isPresent()

        when:
        response = unnaxService.transferOut(req2)

        then:
        !response.isPresent()

        when:
        def transferOut = unnaxService.findRequiredTransferOut(TransferAutoQuery.byOrderCode(req2.orderCode))

        then:
        transferOut
        with(transferOut) {
            amount == req2.getAmount()
            destinationAccount == IBAN
            customerCode == req2.getCustomerCode()
            orderCode == req2.getOrderCode()
            status == TransferAutoStatus.ERROR
            transferType == TransferAutoType.STANDARD
            JsonUtils.readValue(errorDetails, UnnaxErrorResponse.class) == new UnnaxErrorResponse()
                .setStatus("fail")
                .setDetail("")
                .setData(JsonUtils.readTree("{ \"bank_order_code\": \"Bank order code already exists\" }"))
        }

    }

    def "Cancel Transfer"() {
        given:
        def orderCode = UUID.randomUUID().toString()
        def req = new TransferAutoRequest()
            .setAmountInEuros(BigDecimal.TEN)
            .setDestinationAccount(IBAN)
            .setCustomerCode("C123123")
            .setBankOrderCode(UUID.randomUUID().toString())
            .setCustomerNames("Jose")
            .setOrderCode(orderCode)

        when:
        def response = unnaxService.transferOut(req)

        then:
        response.isPresent()

        when:
        unnaxService.cancelTransfer(orderCode)

        then:
        noExceptionThrown()
    }

    def "Sync Transfer"() {
        given:
        def orderCode = UUID.randomUUID().toString()
        def req = new TransferAutoRequest()
            .setAmountInEuros(BigDecimal.TEN)
            .setDestinationAccount(IBAN)
            .setCustomerCode("C123123")
            .setBankOrderCode(UUID.randomUUID().toString())
            .setCustomerNames("Jose")
            .setOrderCode(orderCode)

        when:
        def response = unnaxService.transferOut(req)

        then:
        response.isPresent()

        when:
        unnaxService.syncTransfer(orderCode)

        then:
        noExceptionThrown()
    }

    def "transfer auto - request not valid"() {
        given:
        def req = new TransferAutoRequest()
        req.orderCode = ""

        when:
        unnaxService.transferOut(req)

        then:
        def ex = thrown ConstraintViolationException
        ex.constraintViolations.size() == 5
    }
}
