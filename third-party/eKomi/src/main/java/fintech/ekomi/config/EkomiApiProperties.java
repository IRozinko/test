package fintech.ekomi.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@Accessors(chain = true)
@ConfigurationProperties(prefix = "ekomiApi")
public class EkomiApiProperties {

    private String id = "33450";
    private String key = "11e0d6f998d83648cc635e362";
    private String url = "http://api.ekomi.de/v3/";
    private String version = "cust-1.0.0";
    private String type = "json";

}
