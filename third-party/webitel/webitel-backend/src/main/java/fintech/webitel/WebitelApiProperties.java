package fintech.webitel;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@Accessors(chain = true)
@ConfigurationProperties(prefix = "webitelApi")
public class WebitelApiProperties {

    public static final String MOCK_PROVIDER_NAME = "mock-webitel-provider";
    public static final String PROD_PROVIDER_NAME = "webitel-provider";

    private String url = "https://cloud-fr1.webitel.com/engine";
    private String autoAnswerParam = "";
}
