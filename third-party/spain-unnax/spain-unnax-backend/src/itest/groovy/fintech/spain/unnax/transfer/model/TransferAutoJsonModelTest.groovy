package fintech.spain.unnax.transfer.model

import fintech.testing.integration.JsonBaseSpecification
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.json.JacksonTester
import org.springframework.core.io.Resource

import java.time.LocalDate
import java.time.LocalTime

class TransferAutoJsonModelTest extends JsonBaseSpecification {

    @Autowired
    JacksonTester<TransferAutoRequest> transferAutoRequestTester

    @Value("classpath:transferAutoRequest.json")
    Resource transferAutoRequest

    @Autowired
    JacksonTester<TransferAutoResponse> transferAutoResponseTester

    @Value("classpath:transferAutoResponse.json")
    Resource transferAutoResponse

    @Autowired
    JacksonTester<TransferAutoDetails> transferAutoDetailsTester

    @Value("classpath:transferAutoDetails.json")
    Resource transferAutoDetailsResponse

    void "TransferAutoRequest"() {
        TransferAutoRequest out = new TransferAutoRequest()
            .setAmount(15500)
            .setCurrency("EUR")
            .setCustomerCode("1")
            .setCustomerNames("Jon Doe")
            .setConcept("Unnax test transaction")
            .setOrderCode("111")
            .setBankOrderCode("222")
            .setDestinationAccount("ES1521047047332490142917")

        expect:
        Assertions.assertThat(transferAutoRequestTester.write(out)).isEqualToJson(transferAutoRequest)

        and:
        Assertions.assertThat(transferAutoRequestTester.read(transferAutoRequest)).isEqualTo(out)
    }

    void "TransferAutoResponse"() {
        TransferAutoResponse response = new TransferAutoResponse()
            .setDestinationAccount("ES9220389232143123456790")
            .setBankOrderCode("asasdfdf")
            .setCurrency("EUR")
            .setTime(LocalTime.of(8, 31, 7))
            .setAmount(400)
            .setCustomerCode("1")
            .setOrderCode("123")
            .setSourceAccount("ES0800490766712312546987")
            .setDate(LocalDate.of(2017, 8, 18))

        expect:
        Assertions.assertThat(transferAutoResponseTester.write(response)).isEqualToJson(transferAutoResponse)

        and:
        Assertions.assertThat(transferAutoResponseTester.read(transferAutoResponse)).isEqualTo(response)
    }

    def "TransferAutoDetails"() {
        TransferAutoDetails response = TransferAutoDetails.builder()
            .orderCode("orderCode1")
            .bankOrderCode("bankOrderCode1")
            .amount(10)
            .currency("EUR")
            .concept("concept")
            .sourceIp("127.0.0.1")
            .customerCode("1")
            .customerNames("Name Surname")
            .sourceAccount("ES0800490766712312546987")
            .destinationAccount("ES9220389232143123456790")
            .callbackUrl(null)
            .state(TransferAutoState.COMPLETED)
            .build()

        expect:
        Assertions.assertThat(transferAutoDetailsTester.write(response)).isEqualToJson(transferAutoDetailsResponse)

        and:
        Assertions.assertThat(transferAutoDetailsTester.read(transferAutoDetailsResponse)).isEqualTo(response)

    }
}
