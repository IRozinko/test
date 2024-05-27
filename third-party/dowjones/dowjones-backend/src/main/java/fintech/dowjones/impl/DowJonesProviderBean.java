package fintech.dowjones.impl;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import fintech.dowjones.DowJonesRequestData;
import fintech.dowjones.DowJonesResponseData;
import fintech.dowjones.model.search.name.NameSearchResult;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(DowJonesProviderBean.NAME)
public class DowJonesProviderBean implements DowJonesProvider {

    public static final String NAME = "live-dowjones";

    @Value("${dowjones.url:https://djrc.api.test.dowjones.com/}")
    private String url;

    @Value("${dowjones.namespace:25}")
    private String namespace;

    @Value("${dowjones.username:user1}")
    private String username;

    @Value("${dowjones.password:password1}")
    private String password;

    public final String version = "v1";

    @Autowired
    private DowJonesObjectSerializer dowJonesObjectSerializer;

    @Override
    public DowJonesResponseData search(DowJonesRequestData request) {
        return executeRequest(
            httpGet("/search/name", request)
        );
    }

    private DowJonesResponseData executeRequest(HttpRequestBase request) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (CloseableHttpResponse httpResponse = newHttpClient().execute(request)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
            if (statusCode != HttpStatus.SC_OK) {
                log.info("DowJones: failed to get response: {}", responseBody);
                return DowJonesResponseData.error(request.getURI().toString(), statusCode,
                    responseBody, "Invalid status code: " + statusCode);
            }
            DowJonesResponseData response = DowJonesResponseData.ok(request.getURI().toString(), statusCode, responseBody);
            InputStream targetStream = new ByteArrayInputStream(responseBody.getBytes());
            JAXBElement object = (JAXBElement) dowJonesObjectSerializer.unmarshal(targetStream);
            NameSearchResult nameSearchResult = (NameSearchResult) object.getValue();
            response.setNameSearchResult(nameSearchResult);
            return response;
        } catch (Exception e) {
            return DowJonesResponseData.error(request.getURI().toString(), 0,
                null, Throwables.getRootCause(e).getMessage());
        } finally {
            log.info("Completed DowJones request {} in {} ms", request.getURI().toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @SneakyThrows
    private HttpGet httpGet(@NonNull String path, DowJonesRequestData request) {
        Map<String, String> p = request.getParameters();
        List<NameValuePair> params = new ArrayList<>();
        p.forEach((k, v) -> params.add(new BasicNameValuePair(k, v)));
        URI uri = new URIBuilder(new URI(url + version + path)).setParameters(params).build();
        HttpGet get = new HttpGet(uri);
        String authHeader = "Basic " + token();
        get.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        return get;
    }

    private CloseableHttpClient newHttpClient() {
        return HttpClientBuilder.create().build();
    }

    private String token() {
        String token = username + ":" + password;
        return Base64.getEncoder().encodeToString(token.getBytes(Charset.defaultCharset()));
    }

}
