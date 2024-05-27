package fintech.ga.impl;

import fintech.ga.GADataSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(GAHttpDataSender.NAME)
@RequiredArgsConstructor
@Slf4j
public class GAHttpDataSender implements GADataSender {

    public static final String NAME = "http-data-sender";

    private HttpClient client = HttpClients.custom()
        .disableCookieManagement()
        .disableDefaultUserAgent()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setConnectTimeout(5_000)
            .setSocketTimeout(5_000)
            .setConnectionRequestTimeout(2_000)
            .build())
        .build();

    @Override
    public GAResponse sendData(GARequest request) throws IOException {
        Map<String, String> p = request.getParameters();
        List<NameValuePair> params = new ArrayList<>();
        p.forEach((k, v) -> params.add(new BasicNameValuePair(k, v)));
        try {
            URI uri = new URIBuilder(request.getServiceUrl()).setParameters(params).build();
            HttpPost httpPost = new HttpPost();
            httpPost.addHeader("User-Agent", request.getUserAgent());
            httpPost.setURI(uri);
            log.info("Executing GA request: {}", httpPost.getRequestLine());
            HttpResponse response = client.execute(httpPost);
            return new GAResponse(response.getStatusLine().getStatusCode(), IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
