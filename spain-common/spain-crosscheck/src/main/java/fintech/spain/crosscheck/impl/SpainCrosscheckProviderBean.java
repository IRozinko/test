package fintech.spain.crosscheck.impl;

import com.google.common.base.Stopwatch;
import fintech.JsonUtils;
import fintech.spain.crosscheck.model.SpainCrosscheckInput;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Setter
@Slf4j
@Component(SpainCrosscheckProviderBean.NAME)
public class SpainCrosscheckProviderBean implements SpainCrosscheckProvider {

    public static final String NAME = "spain-crosscheck-provider";

    @Value("${spain.crosscheck.url:http://localhost:8080/api/internal/crosscheck/client}")
    private String url;

    @Value("${spain.crosscheck.apiKey:fake}")
    private String apiKey;

    @SneakyThrows
    @Override
    public SpainCrosscheckResponse request(SpainCrosscheckInput input) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (CloseableHttpClient httpClient = buildHttpClient()) {
            HttpPost post = buildHttpPost(input);
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                int statusCode = response.getStatusLine().getStatusCode();
                SpainCrosscheckResponse resp = new SpainCrosscheckResponse();
                resp.setResponseStatusCode(statusCode);
                resp.setResponseBody(body);
                if (statusCode == 200) {
                    SpainCrosscheckResponse.Attributes attributes = JsonUtils.readValue(body, SpainCrosscheckResponse.Attributes.class);
                    resp.setAttributes(attributes);
                    return resp;
                } else {
                    resp.setError(true);
                    resp.setErrorMessage("Invalid status code: " + statusCode);
                    return resp;
                }
            }
        } finally {
            log.info("Completed Crosscheck request: [{}] in {} ms", input.toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private HttpPost buildHttpPost(SpainCrosscheckInput input) {
        HttpPost post = new HttpPost(url);
        post.addHeader("Authorization", apiKey);
        String json = JsonUtils.writeValueAsString(input);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return post;
    }

    private CloseableHttpClient buildHttpClient() {
        return HttpClientBuilder.create().build();
    }
}
