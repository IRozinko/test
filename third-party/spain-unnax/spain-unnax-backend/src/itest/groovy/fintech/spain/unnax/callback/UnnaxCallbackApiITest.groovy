package fintech.spain.unnax.callback

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.spain.unnax.UnnaxPayOutService
import fintech.spain.unnax.callback.model.BankStatementsUploadedData
import fintech.spain.unnax.callback.model.CallbackRequest
import fintech.spain.unnax.callback.model.TransferAutoCreatedCallbackData
import fintech.spain.unnax.callback.model.TransferAutoProcessedCallbackData
import fintech.spain.unnax.db.Entities
import fintech.spain.unnax.db.TransferAutoStatus
import fintech.spain.unnax.db.UnnaxCallbackRepository
import fintech.spain.unnax.event.BankStatementsUploadedEvent
import fintech.spain.unnax.event.TransferAutoCreatedEvent
import fintech.spain.unnax.event.TransferAutoProcessedEvent
import fintech.spain.unnax.model.TransferAutoQuery
import fintech.spain.unnax.model.WebHookEvents
import fintech.spain.unnax.transfer.TransferAutoUnnaxClient
import fintech.spain.unnax.transfer.impl.MockTransferAutoUnnaxClient
import fintech.spain.unnax.transfer.model.TransferAutoRequest
import fintech.testing.integration.ApiAbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

import java.time.LocalDateTime

import static fintech.spain.unnax.callback.UnnaxCallbackApi.UNNAX_CALLBACK_ENDPOINT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UnnaxCallbackApiITest extends ApiAbstractBaseSpecification {

    static final CUSTOMER_IBAN = "ES6821000844240200657804"
    static final SOURCE_IBAN = "ES1801821797340203757723"

    def conditions = new PollingConditions(timeout: 5, delay: 1)

    @Autowired
    UnnaxCallbackRepository callbackRepository

    @Autowired
    UnnaxPayOutService unnaxService

    @Autowired
    TransferAutoUnnaxClient unnaxClient

    def "ProcessCallback - unknown event"() {
        given:
        def request = new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")

        expect:
        mockMvc.perform(post(UNNAX_CALLBACK_ENDPOINT + "test_event")
            .content(JsonUtils.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(400))
            .andExpect(MockMvcResultMatchers.content().string("Unnax Callback Event is not supported {test_event}."))

        when:
        def entity = callbackRepository.findOne(Entities.callBack.responseId.eq("1234"))

        then:
        entity
        with(entity) {
            responseId == "1234"
            signature == "ea59b3c20c590ba9a889ddb8c7be5c87ee95db85"
        }
    }

    def "ProcessCallback - EVENT_PAYMENT_TRANSFER_AUTO_CREATED"() {
        given:
        MockTransferAutoUnnaxClient.SEND_CREATED_CALLBACK = false
        createTransferOut("123123")
        def data = new TransferAutoCreatedCallbackData()
            .setAmount(100)
            .setOrderId("123123")
            .setDate(TimeMachine.today())
            .setTime(TimeMachine.now().toLocalTime())
            .setCustomerAccount(CUSTOMER_IBAN)
            .setSourceAccount(SOURCE_IBAN)
            .setCurrency("EUR")
            .setCustomerId("C123123")

        def request = new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")
            .setTriggeredEvent(WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_CREATED.name())
            .setDate(TimeMachine.now().withNano(0))
            .setTraceIdentifier("1111")
            .setEnvironment("TEST")
            .setData(JsonUtils.readTree(data))

        expect:
        mockMvc.perform(post(UNNAX_CALLBACK_ENDPOINT + WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_CREATED.name())
            .content(JsonUtils.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(204))

        and:
        conditions.eventually {
            assert eventConsumer.containsEvent(TransferAutoCreatedEvent.class)
        }

        when:
        def event = eventConsumer.getEventOfType(TransferAutoCreatedEvent.class)

        then:
        with(event) {
            responseId == request.responseId
            timestamp == LocalDateTime.of(data.getDate(), data.getTime())
            amount == BigDecimal.valueOf(1);
            customerAccount == CUSTOMER_IBAN
            customerId == data.customerId
            orderId == data.orderId
            currency == data.currency
            sourceAccount == data.sourceAccount
        }

        when:
        def entity = callbackRepository.findOne(Entities.callBack.responseId.eq(event.getResponseId()))

        then:
        entity
        with(entity) {
            entity.event == request.triggeredEvent
            date == request.date
            signature == request.signature
            responseId == request.responseId
            traceIdentifier == request.traceIdentifier
            environment == request.environment
            entity.data == request.getDataAsText()
        }

        cleanup:
        MockTransferAutoUnnaxClient.SEND_CREATED_CALLBACK = true
    }

    def "ProcessCallback - EVENT_PDFS_UPLOADED_LINK"() {
        given:
        def data = new BankStatementsUploadedData().setLink("http://lc:8000/zip.zip").setRequestCode("ABCD").setErrorCode("err");

        def request = new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")
            .setTriggeredEvent(WebHookEvents.EVENT_PDFS_UPLOADED_LINK.name())
            .setDate(TimeMachine.now().withNano(0))
            .setTraceIdentifier("1111")
            .setEnvironment("TEST")
            .setData(JsonUtils.readTree(data))

        expect:
        mockMvc.perform(post(UNNAX_CALLBACK_ENDPOINT + WebHookEvents.EVENT_PDFS_UPLOADED_LINK.name())
            .content(JsonUtils.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(204))

        and:
        conditions.eventually {
            assert eventConsumer.containsEvent(BankStatementsUploadedEvent.class)
        }

        when:
        def event = eventConsumer.getEventOfType(BankStatementsUploadedEvent.class)

        then:
        with(event) {
            responseId == request.responseId
            event.link == data.link
            event.requestCode == data.requestCode
        }

        when:
        def entity = callbackRepository.findOne(Entities.callBack.responseId.eq(event.getResponseId()))

        then:
        entity
        with(entity) {
            entity.event == request.triggeredEvent
            date == request.date
            signature == request.signature
            responseId == request.responseId
            traceIdentifier == request.traceIdentifier
            environment == request.environment
            entity.data == request.getDataAsText()
        }
    }


    def "ProcessCallback - EVENT_PAYMENT_TRANSFER_AUTO_PROCESSED"() {
        given:
        createTransferOut("123123")
        def data = new TransferAutoProcessedCallbackData()
            .setSuccess(true)
            .setProduct("movex_dbt")
            .setOrderId("123123")
            .setBankOrderId("2222")
            .setDate(TimeMachine.today())
            .setTime(TimeMachine.now().toLocalTime())
            .setAmount(100)
            .setCurrency("EUR")
            .setCustomerId("C123123")
            .setCustomerAccount(CUSTOMER_IBAN)
            .setSourceAccount(SOURCE_IBAN)
            .setSrcAccountBalance(1000)
            .setCancelled(false)
            .setSourceBankId(1L)

        def request = new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")
            .setData(JsonUtils.readTree(data))

        expect:
        mockMvc.perform(post(UNNAX_CALLBACK_ENDPOINT + WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_PROCESSED.name())
            .content(JsonUtils.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(204))

        and:
        conditions.eventually {
            assert eventConsumer.containsEvent(TransferAutoProcessedEvent.class)
        }

        when:
        def event = eventConsumer.getEventOfType(TransferAutoProcessedEvent.class)

        then:
        event
        with(event) {
            responseId == request.responseId
            success == data.success
            product == data.product
            orderId == data.orderId
            bankOrderId == data.bankOrderId
            timestamp == LocalDateTime.of(data.getDate(), data.getTime())
            amount == BigDecimal.ONE
            currency == data.currency
            customerAccount == CUSTOMER_IBAN
            customerId == data.customerId
            sourceAccount == data.getSourceAccount()
            srcAccountBalance == BigDecimal.TEN
            cancelled == data.cancelled
            sourceBankId == data.sourceBankId
        }
    }

    def "ProcessCallback - EVENT_PAYMENT_TRANSFER_AUTO_PROCESSED - Not successful"() {
        given:
        createTransferOut("123123")
        def data = new TransferAutoProcessedCallbackData()
            .setSuccess(false)
            .setOrderId("123123")
            .setBankOrderId("2222")
            .setDate(TimeMachine.today())
            .setTime(TimeMachine.now().toLocalTime())
            .setAmount(100)
            .setCurrency("EUR")
            .setCustomerId("C123123")
            .setCustomerAccount(CUSTOMER_IBAN)
            .setSourceAccount(SOURCE_IBAN)
            .setSrcAccountBalance(1000)
            .setCancelled(false)
            .setErrorCode("S01")
            .setErrorMessage("Error Message")
            .setSourceBankId(1L)

        def request = new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")
            .setData(JsonUtils.readTree(data))

        expect:
        mockMvc.perform(post(UNNAX_CALLBACK_ENDPOINT + WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_PROCESSED.name())
            .content(JsonUtils.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(204))

        and:
        conditions.eventually {
            assert eventConsumer.containsEvent(TransferAutoProcessedEvent.class)
        }

        when:
        def event = eventConsumer.getEventOfType(TransferAutoProcessedEvent.class)

        then:
        event
        with(event) {
            responseId == request.responseId
            success == data.success
            product == data.product
            orderId == data.orderId
            bankOrderId == data.bankOrderId
            timestamp == LocalDateTime.of(data.getDate(), data.getTime())
            amount == BigDecimal.ONE
            currency == data.currency
            customerAccount == CUSTOMER_IBAN
            customerId == data.customerId
            sourceAccount == data.getSourceAccount()
            srcAccountBalance == BigDecimal.TEN
            cancelled == data.cancelled
            sourceBankId == data.sourceBankId
        }
    }

    def "handleTransferAutoCreated"() {
        given:
        createTransferOut("123123")

        and:
        def data = new TransferAutoCreatedCallbackData()
            .setAmount(100)
            .setOrderId("123123")
            .setDate(TimeMachine.today())
            .setTime(TimeMachine.now().toLocalTime())
            .setCustomerAccount(CUSTOMER_IBAN)
            .setSourceAccount(SOURCE_IBAN)
            .setCurrency("EUR")
            .setCustomerId("C123123")

        def request = new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")
            .setTriggeredEvent(WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_CREATED.name())
            .setDate(TimeMachine.now().withNano(0))
            .setTraceIdentifier("1111")
            .setEnvironment("TEST")
            .setData(JsonUtils.readTree(data))

        expect:
        mockMvc.perform(post(UNNAX_CALLBACK_ENDPOINT + WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_CREATED.name())
            .content(JsonUtils.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(204))

        and:
        conditions.eventually {
            assert eventConsumer.containsEvent(TransferAutoCreatedEvent.class)
        }
        sleep(500)

        when:
        def outEntity = unnaxService.findRequiredTransferOut(TransferAutoQuery.byOrderCode("123123"))

        then:
        outEntity.status == TransferAutoStatus.CREATED
        outEntity.sourceAccount == data.getSourceAccount()
        outEntity.orderCreatedAt

    }

    @Unroll
    def "handleTransferAutoProcessed"() {
        given:
        createTransferOut("123123")

        and:
        def data = new TransferAutoProcessedCallbackData()
            .setSuccess(success)
            .setProduct("movex_dbt")
            .setOrderId("123123")
            .setBankOrderId("2222")
            .setDate(TimeMachine.today())
            .setTime(TimeMachine.now().toLocalTime())
            .setAmount(100)
            .setCurrency("EUR")
            .setCustomerId("C123123")
            .setCustomerAccount(CUSTOMER_IBAN)
            .setSourceAccount(SOURCE_IBAN)
            .setSrcAccountBalance(1000)
            .setCancelled(cancelled)
            .setSourceBankId(1L)

        def request = new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")
            .setData(JsonUtils.readTree(data))

        expect:
        mockMvc.perform(post(UNNAX_CALLBACK_ENDPOINT + WebHookEvents.EVENT_PAYMENT_TRANSFER_AUTO_PROCESSED.name())
            .content(JsonUtils.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(204))

        and:
        conditions.eventually {
            assert eventConsumer.containsEvent(TransferAutoProcessedEvent.class)
        }
        sleep(500)

        when:
        def outEntity = unnaxService.findRequiredTransferOut(TransferAutoQuery.byOrderCode("123123"))

        then:
        outEntity.status == transferStatus
        outEntity.sourceAccount == data.getSourceAccount()
        outEntity.orderProcessedAt

        where:
        success | cancelled | transferStatus
        true    | false     | TransferAutoStatus.PROCESSED
        false   | true      | TransferAutoStatus.CANCELED
    }

    def createTransferOut(String orderCode) {
        def req = new TransferAutoRequest()
            .setAmountInEuros(BigDecimal.TEN)
            .setDestinationAccount(CUSTOMER_IBAN)
            .setCustomerCode("C123123")
            .setOrderCode(orderCode)
            .setCurrency("EUR")
            .setCustomerNames("Jose")
            .setConcept("concept")
            .setTags(Arrays.asList("1", "2"))
        def resp = unnaxService.transferOut(req)
    }

}
