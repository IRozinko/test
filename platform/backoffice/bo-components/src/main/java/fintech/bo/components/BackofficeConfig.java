package fintech.bo.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class BackofficeConfig {

    @Value("${bo.baseUrl:http://localhost:8090}")
    private String baseUrl;

    @Autowired
    private Environment environment;

    public String getBaseUrl() {
        return baseUrl;
    }

    @PostConstruct
    private void logEnvironment() {
        logEnvironmentVariable("spring.config.location");
        logEnvironmentVariable("logging.config");
    }

    private void logEnvironmentVariable(String name) {
        if (environment.containsProperty(name)) {
            log.info("Environment variable " + name + " = {}", environment.getProperty(name));
        } else {
            log.info("No {} variable found ", name);
        }
    }
}
