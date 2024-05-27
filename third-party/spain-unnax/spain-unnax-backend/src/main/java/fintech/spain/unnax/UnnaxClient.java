package fintech.spain.unnax;

import fintech.spain.unnax.model.UnnaxErrorResponse;
import fintech.spain.unnax.model.UnnaxResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static fintech.JsonUtils.readValue;

@Slf4j
public abstract class UnnaxClient {

    protected final RestTemplate restTemplate;

    public UnnaxClient(@Autowired @Qualifier("unnaxClient") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected <T> UnnaxResponse<T> getIfSuccess(ResponseEntity<String> response, int code, Class<T> respClass) {
        if (response.getStatusCodeValue() == code)
            return new UnnaxResponse<>(readValue(response.getBody(), respClass));
        else if (response.getStatusCode().is4xxClientError()){
            log.error("Can't execute request to Unnax API {}", response.getBody());
            return new UnnaxResponse<>(readValue(response.getBody(), UnnaxErrorResponse.class));
        } else {
            log.error("Can't execute request to Unnax API {}", response.getBody());
            return new UnnaxResponse<>(new UnnaxErrorResponse(response.getBody()));
        }
    }

}
