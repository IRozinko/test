package fintech.google.recaptcha.impl;

import fintech.JsonUtils;
import fintech.Validate;
import fintech.google.recaptcha.GoogleRecaptchaService;
import fintech.google.recaptcha.RecaptchaResponse;
import fintech.google.recaptcha.VerifyCaptchaCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GoogleRecaptchaServiceBean implements GoogleRecaptchaService {

    @Value("${google.recaptcha.secret:6Lfv2RgUAAAAANK-i5MyUhYROldUg7J9Dq11eUMo}")
    private String secret;

    @Value("${google.recaptcha.url:https://www.google.com/recaptcha/api/siteverify}")
    private String recaptchaUrl;

    private HttpClient httpClient = HttpClientBuilder.create().build();

    @Override
    public boolean isResponseValid(VerifyCaptchaCommand command) {
        Validate.notNull(command.getRecaptchaResponse(), "Google recaptcha response required");
        HttpPost httpPost = new HttpPost(this.recaptchaUrl);
        List<NameValuePair> params = buildParams(command);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("Recaptcha request failed");
                return false;
            }
            String body = EntityUtils.toString(response.getEntity());
            RecaptchaResponse recaptchaResponse = JsonUtils.readValue(body, RecaptchaResponse.class);

            return recaptchaResponse.isSuccess();
        } catch (IOException e) {
            log.error("Recaptcha request failed", e);
            return false;
        }
    }

    private List<NameValuePair> buildParams(VerifyCaptchaCommand command) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("secret", this.secret));
        params.add(new BasicNameValuePair("response", command.getRecaptchaResponse()));
        if (!StringUtils.isBlank(command.getIpAddress())) {
            params.add(new BasicNameValuePair("remoteip", command.getIpAddress()));
        }
        return params;
    }
}
