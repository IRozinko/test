package fintech.testing.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fintech.JsonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("itest")
@Configuration
public class TestJacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtils.getMapper();
    }
}
