package fintech.nordigen.impl;

import fintech.JsonUtils;
import fintech.Validate;
import fintech.nordigen.json.NordigenJson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component(NordigenProviderBean.NAME)
public class NordigenProviderBean implements NordigenProvider {

    public static final String NAME = "nordigen-provider";

    private final String user;
    private final String password;
    private String url;

    public NordigenProviderBean(@Value("${nordigen.url:https://demo.nordigen.com/api/process/factors/flags}") String url,
                                @Value("${nordigen.user:1337000138}") String user,
                                @Value("${nordigen.password:6f430c085ddc9d7c5945adac80a02ce1}") String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @SneakyThrows
    @Override
    public NordigenResponse request(Long clientId, String requestBody) {
        try (CloseableHttpClient httpclient = buildHttpClient()) {
            HttpPost post = buildHttpPost(requestBody);
            try (CloseableHttpResponse response = httpclient.execute(post)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.debug("Nordigen response body: {}", body);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    return NordigenResponse.error(statusCode, body, "Invalid response status: " + statusCode);
                }
                NordigenJson json = JsonUtils.readValue(body, NordigenJson.class);
                Validate.notNull(json.getAccountList(), "No account list returned");
                Validate.notEmpty(json.getAccountList(), "No account list returned");
                Validate.notNull(json.getAccountList().get(0).getFactors(), "No factors returned");
                return NordigenResponse.ok(statusCode, body);
            }
        }
    }

    private CloseableHttpClient buildHttpClient() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);
        provider.setCredentials(AuthScope.ANY, credentials);
        return HttpClientBuilder.create()
            .setDefaultCredentialsProvider(provider)
            .build();
    }

    private HttpPost buildHttpPost(String requestBody) {
        HttpPost post = new HttpPost(url);
        HttpEntity entity = buildEntity(requestBody);
        post.setEntity(entity);
        return post;
    }

    private HttpEntity buildEntity(String requestBody) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file", requestBody.getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_JSON, "statement.json");
        return builder.build();
    }
}
