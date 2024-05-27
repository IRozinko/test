package fintech.spain.equifax.json;

import com.google.common.base.Stopwatch;
import fintech.JsonUtils;
import fintech.spain.equifax.impl.EquifaxProvider;
import fintech.spain.equifax.json.client.EquifaxApi;
import fintech.spain.equifax.json.client.EquifaxJsonRequest;
import fintech.spain.equifax.json.client.EquifaxJsonResponse;
import fintech.spain.equifax.model.EquifaxRequest;
import fintech.spain.equifax.model.EquifaxResponse;
import fintech.spain.equifax.model.EquifaxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component(EquifaxJsonProviderBean.NAME)
@ConditionalOnProperty(name = "spain.equifax.provider", havingValue = EquifaxJsonProviderBean.NAME)
public class EquifaxJsonProviderBean implements EquifaxProvider {

    private static final String SPAIN_COUNTRY_CODE = "724";
    private static final String DOC_TYPE_ID = "ID";
    private static final String PERSON_TYPE = "F";

    public static final String NAME = "json-spain-equifax-provider";

    private final EquifaxApi api;
    private final EquifaxJsonResponseParser parser;

    @Override
    public EquifaxResponse request(EquifaxRequest request) throws IOException {
        EquifaxJsonRequest requestDto = new EquifaxJsonRequest(SPAIN_COUNTRY_CODE, DOC_TYPE_ID, PERSON_TYPE, request);
        Response<ResponseBody> response;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            response = api.request(requestDto).execute();
        } finally {
            log.info("Completed Equifax Json request: [params: {}] in {} ms", request, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        if (!response.isSuccessful()) {
            return new EquifaxResponse()
                .setStatus(EquifaxStatus.ERROR)
                .setError(response.message())
                .setResponseBody(response.errorBody().string());
        }

        String rawResponse = response.body().string();
        try {
            EquifaxJsonResponse equifaxJsonResponse = JsonUtils.readValue(rawResponse, EquifaxJsonResponse.class);
            EquifaxResponse parsedResponse = parser.parse(equifaxJsonResponse);
            parsedResponse.setRequestBody(JsonUtils.writeValueAsString(requestDto));
            parsedResponse.setResponseBody(rawResponse);
            return parsedResponse;
        } catch (IllegalStateException e) {
            log.error("Error parsing response for equifax request [" + request.toString() + "]", e);
            return new EquifaxResponse()
                .setStatus(EquifaxStatus.ERROR)
                .setError(e.getMessage())
                .setResponseBody(rawResponse);
        }
    }


}
