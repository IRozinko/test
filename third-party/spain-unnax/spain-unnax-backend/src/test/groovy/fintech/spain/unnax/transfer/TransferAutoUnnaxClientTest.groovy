package fintech.spain.unnax.transfer

import fintech.JsonUtils
import fintech.spain.unnax.UnnaxClientSpecification
import fintech.spain.unnax.model.UnnaxErrorResponse
import fintech.spain.unnax.transfer.impl.TransferAutoUnnaxClientImpl
import fintech.spain.unnax.transfer.model.TransferAutoRequest
import fintech.spain.unnax.transfer.model.TransferAutoResponse
import fintech.spain.unnax.transfer.model.TransferAutoType
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo

class TransferAutoUnnaxClientTest extends UnnaxClientSpecification {

    def "TransferOut"() {
        given:
        TransferAutoUnnaxClient outUnnaxClient = new TransferAutoUnnaxClientImpl(restTemplate)
        TransferAutoRequest request = new TransferAutoRequest()
            .setAmount(100)
            .setBankOrderCode("bank_order_code")
            .setOrderCode("order_code")
            .setConcept("concept")
            .setCustomerCode("customer_code")
            .setTransferType(TransferAutoType.STANDARD)

        TransferAutoResponse response = new TransferAutoResponse()

        expect:
        mockServer.expect(requestTo(UnnaxClientSpecification.API_URI + "/api/v3/payment/transfer/auto/"))
            .andExpect(MockRestRequestMatchers.header("Authorization",
            "Unnax " + Base64.encoder.encodeToString(String.join(":", UnnaxClientSpecification.API_ID, UnnaxClientSpecification.API_CODE).getBytes())))
            .andRespond(
            MockRestResponseCreators.withCreatedEntity(new URI(""))
                .body(JsonUtils.writeValueAsString(response))
        )

        when:
        def responseEntity = outUnnaxClient.transferAuto(request)

        then:
        !responseEntity.error
        response == responseEntity.getResponse()
        mockServer.verify()
    }

    def "TransferOut - error"() {
        given:
        TransferAutoUnnaxClient outUnnaxClient = new TransferAutoUnnaxClientImpl(restTemplate)
        TransferAutoRequest request = new TransferAutoRequest()
            .setAmount(100)
            .setBankOrderCode("bank_order_code")
            .setOrderCode("order_code")
            .setConcept("concept")
            .setCustomerCode("customer_code")
            .setTransferType(TransferAutoType.STANDARD)

        UnnaxErrorResponse response = new UnnaxErrorResponse().setStatus("fail")

        expect:
        mockServer.expect(requestTo(UnnaxClientSpecification.API_URI + "/api/v3/payment/transfer/auto/"))
            .andExpect(MockRestRequestMatchers.header("Authorization",
            "Unnax " + Base64.encoder.encodeToString(String.join(":", UnnaxClientSpecification.API_ID, UnnaxClientSpecification.API_CODE).getBytes())))
            .andRespond(MockRestResponseCreators.withBadRequest().body(JsonUtils.writeValueAsString(response)))

        when:
        def responseEntity = outUnnaxClient.transferAuto(request)

        then:
        responseEntity.error
        response == responseEntity.getErrorResponse()
        mockServer.verify()
    }

}
