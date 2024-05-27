package fintech.viventor.impl;

import com.google.common.base.Stopwatch;
import fintech.JsonUtils;
import fintech.viventor.model.PostLoanExtensionRequest;
import fintech.viventor.model.PostLoanPaidRequest;
import fintech.viventor.model.PostLoanPaymentRequest;
import fintech.viventor.model.PostLoanRequest;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
@Component(ViventorProviderBean.NAME)
class ViventorProviderBean implements ViventorProvider {

    public static final String NAME = "viventor-provider";

    private final String url;

    private final String token;

    public ViventorProviderBean(
        @Value("${viventor.url:https://api.stage.viventor.com}") String url,
        @Value("${viventor.token:123456}") String token) {
        this.url = notBlank(url);
        this.token = notBlank(token);
    }

    @SneakyThrows
    @Override
    public ViventorResponse postLoan(PostLoanRequest postLoanRequest) {
        return executeRequest(
            httpPost("/loans", JsonUtils.writeValueAsString(postLoanRequest))
        );
    }

    @SneakyThrows
    @Override
    public ViventorResponse postLoanPaid(String viventorLoanId, PostLoanPaidRequest postLoanPaidRequest) {
        return executeRequest(
            httpPost(
                String.format("/loans/%s/paid", viventorLoanId),
                JsonUtils.writeValueAsString(postLoanPaidRequest)
            )
        );
    }

    @SneakyThrows
    @Override
    public ViventorResponse postLoanPayment(String viventorLoanId, PostLoanPaymentRequest postLoanPaidRequest) {
        return executeRequest(
            httpPost(
                String.format("/loans/%s/payments", viventorLoanId),
                JsonUtils.writeValueAsString(postLoanPaidRequest)
            )
        );
    }

    @SneakyThrows
    @Override
    public ViventorResponse postLoanExtension(String viventorLoanId, PostLoanExtensionRequest postLoanExtensionRequest) {
        return executeRequest(
            httpPost(
                String.format("/loans/%s/extension", viventorLoanId),
                JsonUtils.writeValueAsString(postLoanExtensionRequest)
            )
        );
    }

    @SneakyThrows
    @Override
    public ViventorResponse getLoan(String viventorLoanId) {
        return executeRequest(
            httpGet(String.format("/loans/%s", viventorLoanId))
        );
    }

    private ViventorResponse executeRequest(HttpRequestBase request) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (CloseableHttpResponse httpResponse = newHttpClient().execute(request)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
            if (statusCode != HttpStatus.SC_OK) {
                return ViventorResponse.error(request.getURI().toString(), statusCode,
                    responseBody, "Invalid status code: " + statusCode);
            }
            return ViventorResponse.ok(request.getURI().toString(), statusCode, responseBody);
        } finally {
            log.info("Completed Viventor request: {} in {} ms", request.getURI().toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private CloseableHttpClient newHttpClient() {
        return HttpClientBuilder.create().build();
    }

    @SneakyThrows
    private HttpPost httpPost(@NonNull String path, @NonNull String jsonBody) {
        HttpPost post = new HttpPost(new URI(url + "/" + path).normalize());
        post.addHeader(new BasicHeader("Authorization", token));
        post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        return post;
    }

    @SneakyThrows
    private HttpGet httpGet(@NonNull String path) {
        HttpGet get = new HttpGet(new URI(url + "/" + path).normalize());
        get.addHeader(new BasicHeader("Authorization", token));
        return get;
    }

}
