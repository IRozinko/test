package fintech.sms.altiria;

import fintech.sms.Sms;
import fintech.sms.spi.SmsException;
import fintech.sms.spi.SmsProvider;
import fintech.sms.spi.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// https://www.altiria.com/enviar-sms-con-java/
@Slf4j
@Component(AltiriaSmsProvider.NAME)
public class AltiriaSmsProvider implements SmsProvider {


    //cmd=sendsms&domainId=viasms&login=viasms&passwd=<PASSWORD>&dest=<DESTINATION PHONE NUMBER>&senderId=<SENDER NAME>&msg=<SOME TEXT HERE>" http://www.altiria.net/api/http

    public static final String NAME = "spain-altiria-sms-provider";

    private final String url;
    private final String domainId;
    private final String login;
    private final String password;


    public AltiriaSmsProvider(@Value("${sms.altiria.url:}") String url,
                              @Value("${sms.altiria.domainId:}") String domainId,
                              @Value("${sms.altiria.login:}") String login,
                              @Value("${sms.altiria.password:}") String password) {
        this.url = url;
        this.domainId = domainId;
        this.login = login;
        this.password = password;
    }

    @Override
    public SmsResponse send(Sms sms) {
        try (CloseableHttpClient httpClient = buildHttpClient()) {
            HttpPost post = buildHttpPost(sms);
            try (CloseableHttpResponse response = httpClient.execute(post)) {

                String body = EntityUtils.toString(response.getEntity());

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.info("Altiria request failed, status code: {}", statusCode);
                    throw new SmsException(String.format("Failed to send sms via Altiria, status: %s, result: %s", statusCode, body));
                } else if (body.startsWith("ERROR")) {
                    log.info("Altiria request failed, response: {}", response);
                    throw new SmsException("Failed to send sms via Altiria, result: " + body);
                } else {
                    return SmsResponse.builder()
                        .id(RandomStringUtils.randomAlphanumeric(10))
                        .providerName(NAME)
                        .message("ok").build();
                }
            }
        } catch (IOException e) {
            throw new SmsException("Failed to send sms via Altiria", e);
        }
    }

    private CloseableHttpClient buildHttpClient() {
        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(60000)
            .build();

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(config);
        return builder.build();
    }

    private HttpPost buildHttpPost(Sms sms) {
        HttpPost post = new HttpPost(url);

        List<NameValuePair> parametersList = new ArrayList<>();
        parametersList.add(new BasicNameValuePair("cmd", "sendsms"));
        parametersList.add(new BasicNameValuePair("domainId", this.domainId));
        parametersList.add(new BasicNameValuePair("login", this.login));
        parametersList.add(new BasicNameValuePair("passwd", this.password));
        parametersList.add(new BasicNameValuePair("dest", sms.getTo()));
        parametersList.add(new BasicNameValuePair("msg", sms.getText()));
        parametersList.add(new BasicNameValuePair("senderId", sms.getSenderId()));
        post.setEntity(new UrlEncodedFormEntity(parametersList, StandardCharsets.UTF_8));
        return post;
    }
}
