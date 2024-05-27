package fintech.spain.unnax.config;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class UnnaxClientConfig {

    @Bean("unnaxClient")
    @ConditionalOnProperty(name = "unnax.mock", havingValue = "false")
    public RestTemplate unnaxClient(RestTemplateBuilder builder,
                                    @Value("${unnax.apiId:12345}") String apiId,
                                    @Value("${unnax.apiCode:12345}") String apiCode,
                                    @Value("${unnax.uri}") String uri) {
        return builder
            .rootUri(uri)
            .interceptors(perfRequestSyncInterceptor(), authorizationInterceptor(apiId, apiCode))
            .errorHandler(responseErrorHandler())
            .build();
    }

    private ClientHttpRequestInterceptor authorizationInterceptor(String apiId, String apiCode) {
        return (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Unnax " + token(apiId, apiCode));
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return execution.execute(request, body);
        };
    }

    private ClientHttpRequestInterceptor perfRequestSyncInterceptor() {
        return (hr, bytes, chre) -> {
            Stopwatch stopwatch = Stopwatch.createStarted();
            ClientHttpResponse response = chre.execute(hr, bytes);
            stopwatch.stop();
            log.info("Unnax request [{}] done in {} ms", hr.getURI(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
            return response;
        };
    }

    private static String token(String apiId, String apiCode) {
        String token = apiId + ":" + apiCode;
        return Base64.getEncoder().encodeToString(token.getBytes(Charset.defaultCharset()));
    }

    private ResponseErrorHandler responseErrorHandler() {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            }
        };
    }
}
