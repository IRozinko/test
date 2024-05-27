package manual;

import com.payxpert.connect2pay.utils.Connect2payRESTClient;
import fintech.JsonUtils;
import fintech.payxpert.impl.RebillRequest;
import fintech.payxpert.impl.RebillResponse;

public class ManualRun {

    public static void main(String[] args) throws Exception {
        Connect2payRESTClient httpClient = (new Connect2payRESTClient()).addBasicAuthentication("104772", "g>&2Etw|N<R{m-/q");

        RebillRequest request = new RebillRequest()
            .setAmount(100L)
            .setTransactionID("39720493");

        httpClient.setBody(JsonUtils.writeValueAsString(request));
        httpClient.setUrl("https://api.payxpert.com/transaction/" + request.getTransactionID() + "/rebill");
        String body = httpClient.post();
        System.out.println(body);
        RebillResponse response = JsonUtils.readValue(body, RebillResponse.class);
        System.out.println(response);
    }
}

