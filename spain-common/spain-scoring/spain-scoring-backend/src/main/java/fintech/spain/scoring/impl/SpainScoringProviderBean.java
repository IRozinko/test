package fintech.spain.scoring.impl;

import com.google.common.collect.ImmutableMap;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.spain.scoring.model.ScoringModelType;
import fintech.spain.scoring.model.ScoringRequestCommand;
import fintech.spain.scoring.spi.ScoringResponse;
import fintech.spain.scoring.spi.SpainScoringProvider;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static fintech.BigDecimalUtils.*;

@Slf4j
@Component(SpainScoringProviderBean.NAME)
public class SpainScoringProviderBean implements SpainScoringProvider {

    public static final String NAME = "spain-scoring-provider";

    private final Map<ScoringModelType, String> modelUrls;
    private final String user;
    private final String password;

    public SpainScoringProviderBean(@Value("${spain.scoring.user:model_user}") String user,
                                    @Value("${spain.scoring.password:B6JxBFu9dXoORx6u}") String password,
                                    @Value("${spain.scoring.lrModelUrl:https://model.cubiform.net/LR_model_v2}") String lrModelUrl,
                                    @Value("${spain.scoring.dedicatedModelUrl:https://model.cubiform.net/Dedicated_model}") String dedicatedModelUrl,
                                    @Value("${spain.scoring.creditLimitModelUrl:https://model.cubiform.net/Limit_call_model}") String creditLimitModelUrl) {
        this.user = user;
        this.password = password;
        modelUrls = ImmutableMap.of(
            ScoringModelType.LINEAR_REGRESSION_MODEL, lrModelUrl,
            ScoringModelType.DEDICATED_MODEL, dedicatedModelUrl,
            ScoringModelType.CREDIT_LIMIT_MODEL, creditLimitModelUrl
        );
    }

    @SneakyThrows
    @Override
    public ScoringResponse request(ScoringRequestCommand command) {
        try (CloseableHttpClient httpClient = buildHttpClient()) {
            String url = modelUrls.get(command.getType());
            Validate.notBlank(url, "Url not configured for scoring model type %s", command.getType());
            HttpPost post = buildHttpPost(command.getAttributes(), url);
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.warn("Scoring request failed to url: [{}], status code: [{}], body: [{}]", url, statusCode, body);
                    return ScoringResponse.error(statusCode, body, "Invalid response status: " + statusCode);
                }
                ScoreBody score = JsonUtils.readValue(body, ScoreBody.class);
                return ScoringResponse.ok(statusCode, body, amount(score.getScore()));
            }
        }
    }

    private HttpPost buildHttpPost(Map<String, Object> attributes, String url) {
        String json = JsonUtils.writeValueAsString(attributes);
        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return post;
    }

    private CloseableHttpClient buildHttpClient() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);
        provider.setCredentials(AuthScope.ANY, credentials);
        return HttpClientBuilder.create()
            .setDefaultCredentialsProvider(provider)
            .build();
    }

    @Data
    static class ScoreBody {
        private double score;
    }
}
