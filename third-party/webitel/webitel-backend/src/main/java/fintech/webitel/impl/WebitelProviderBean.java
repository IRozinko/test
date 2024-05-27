package fintech.webitel.impl;

import com.google.common.base.Stopwatch;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.webitel.WebitelApiProperties;
import fintech.webitel.WebitelProvider;
import fintech.webitel.model.WebitelAuthToken;
import fintech.webitel.model.WebitelCallCommand;
import fintech.webitel.model.WebitelCallResult;
import fintech.webitel.model.WebitelLoginCommand;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(WebitelApiProperties.PROD_PROVIDER_NAME)
class WebitelProviderBean implements WebitelProvider {

    private WebitelApiProperties config;

    @Autowired
    public WebitelProviderBean(WebitelApiProperties config) {
        this.config = config;
    }

    @Override
    @SneakyThrows
    public WebitelCallResult originateNewCall(WebitelCallCommand command) {
        Validate.notNull(command.getToken(), "Null token");
        Validate.notNull(command.getKey(), "Null key");
        Validate.notNull(command.getCallFromUser(), "Null call from user");
        Validate.notNull(command.getDestinationNumber(), "Null destination number");

        String phoneNumber = formatPhoneNumber(command.getDestinationNumber());
        log.info("webitel called id [{}], source [{}]", phoneNumber, command.getDestinationNumber());
        OriginateNewCallRequest originateNewCallRequest = new OriginateNewCallRequest()
            .setAutoAnswerParam(config.getAutoAnswerParam())
            .setCalledId(phoneNumber)
            .setCallerId(command.getCallFromUser());

        HttpPost httpRequest = prepareNewCallRequest(
            JsonUtils.writeValueAsString(originateNewCallRequest),
            command.getToken(),
            command.getKey()
        );

        Stopwatch stopwatch = Stopwatch.createStarted();
        try (CloseableHttpResponse httpResponse = buildHttpClient().execute(httpRequest)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
            if (statusCode != HttpStatus.SC_OK) {
                log.error("Failed to originate new call. Status code: {}, Body: {}", statusCode, responseBody);
                throw new RuntimeException("Webitel originate new call failed: " + responseBody);
            }
            return JsonUtils.readValue(responseBody, WebitelCallResult.class);
        } finally {
            log.info("Completed Webitel request: [{}] in {} ms", originateNewCallRequest.toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private String formatPhoneNumber(String destinationNumber) {
        String cleanNumber = destinationNumber.trim()
            .replaceFirst("\\+", "")
            .replaceFirst("34", "");
        return "0034" + cleanNumber;
    }

    @Override
    @SneakyThrows
    public WebitelAuthToken authenticate(WebitelLoginCommand request) {
        try (CloseableHttpClient httpClient = buildHttpClient()) {
            HttpPost post = new HttpPost(new URI(config.getUrl() + "/login").normalize());
            post.setEntity(new StringEntity(JsonUtils.writeValueAsString(request), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode != HttpStatus.SC_OK) {
                    log.error("Failed to originate new call. Status code: {}, Body: {}", statusCode, responseBody);
                    throw new RuntimeException("Webitel login failed: " + responseBody);
                }
                return JsonUtils.readValue(responseBody, WebitelAuthToken.class);
            }
        }
    }

    private CloseableHttpClient buildHttpClient() {
        return HttpClientBuilder.create().build();
    }

    private HttpPost prepareNewCallRequest(String jsonBody, String token, String key) throws Exception {
        HttpPost post = new HttpPost(new URI(config.getUrl() + "/api/v2/channels").normalize());
        post.addHeader(new BasicHeader("X-Access-Token", token));
        post.addHeader(new BasicHeader("X-Key", key));
        post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        return post;
    }
}
