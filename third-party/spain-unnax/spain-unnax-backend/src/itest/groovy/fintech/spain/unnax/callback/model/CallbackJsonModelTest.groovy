package fintech.spain.unnax.callback.model

import fintech.JsonUtils
import fintech.testing.integration.JsonBaseSpecification
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.json.JacksonTester
import org.springframework.core.io.Resource

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CallbackJsonModelTest extends JsonBaseSpecification {

    @Autowired
    JacksonTester<CallbackRequest> callbackRequestTester

    @Value("classpath:callback.json")
    Resource callback

    @Autowired
    JacksonTester<TransferAutoCreatedCallbackData> transferAutoCreatedTester

    @Value("classpath:transferAutoCreatedCallbackData.json")
    Resource transferAutoCreatedCallbackData

    @Autowired
    JacksonTester<TransferAutoProcessedCallbackData> transferAutoProcessedTester

    @Value("classpath:transferAutoProcessedCallbackData.json")
    Resource transferAutoProcessedCallbackData



    def "Callback request"() {
        CallbackRequest request = new CallbackRequest()
            .setData(JsonUtils.readTree("{ \"customer_account\": \"ES1521047047332490142917\" }"))
            .setTriggeredEvent("event_payment_transfer_auto_created")
            .setEnvironment("unnax_integration_aws")
            .setSignature("e8f88f223f7a6269966a74f404fa24ae039302e8")
            .setTraceIdentifier("b9f174fc-7763-4853-96f1-ab8bfdbc66a4")
            .setDate(LocalDateTime.of(2018, 9, 25, 11, 1, 20))
            .setService("payment_transfer_auto")
            .setResponseId("b9f174fc-7763-4853-96f1-ab8bfdbc66a4")

        expect:
        Assertions.assertThat(callbackRequestTester.write(request)).isEqualToJson(callback)

        and:
        Assertions.assertThat(callbackRequestTester.read(callback)).isEqualTo(request)
    }

    def "TransferAutoCreatedCallbackData"() {
        TransferAutoCreatedCallbackData data = new TransferAutoCreatedCallbackData()
            .setCustomerAccount("ES1521047047332490142917")
            .setSourceAccount(null)
            .setCustomerId("1")
            .setDate(LocalDate.of(2018, 9, 25))
            .setCurrency("EUR")
            .setOrderId("1537873308")
            .setTime(LocalTime.of(11, 1, 20))
            .setAmount(15500)

        expect:
        Assertions.assertThat(transferAutoCreatedTester.write(data)).isEqualToJson(transferAutoCreatedCallbackData)

        and:
        Assertions.assertThat(transferAutoCreatedTester.read(transferAutoCreatedCallbackData)).isEqualTo(data)
    }

    def "TransferAutoProccessedCallbackData"() {
        TransferAutoProcessedCallbackData data = new TransferAutoProcessedCallbackData()
            .setSuccess(true)
            .setProduct("movex_dbt")
            .setOrderId("1537873308")
            .setBankOrderId("1537873308")
            .setDate(LocalDate.of(2018, 9, 25))
            .setTime(LocalTime.of(11, 1, 32))
            .setAmount(15500)
            .setCurrency("EUR")
            .setCustomerId("1")
            .setCustomerAccount("ES1521047047332490142917")
            .setSourceAccount("ES8200810593280001203422")
            .setSrcAccountBalance(5228)
            .setCancelled(false)
            .setSourceBankId(3L)

        expect:
        Assertions.assertThat(transferAutoProcessedTester.write(data)).isEqualToJson(transferAutoProcessedCallbackData)

        and:
        Assertions.assertThat(transferAutoProcessedTester.read(transferAutoProcessedCallbackData)).isEqualTo(data)
    }

}
