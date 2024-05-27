package fintech.webitel.impl;

import fintech.TimeMachine;
import fintech.Validate;
import fintech.webitel.WebitelApiProperties;
import fintech.webitel.WebitelProvider;
import fintech.webitel.model.WebitelAuthToken;
import fintech.webitel.model.WebitelCallResult;
import fintech.webitel.model.WebitelLoginCommand;
import fintech.webitel.model.WebitelCallCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Slf4j
@Component(WebitelApiProperties.MOCK_PROVIDER_NAME)
class MockWebitelProvider implements WebitelProvider {

    @Override
    public WebitelCallResult originateNewCall(WebitelCallCommand command) {
        Validate.notNull(command.getToken(), "Null token");
        Validate.notNull(command.getKey(), "Null key");
        Validate.notNull(command.getCallFromUser(), "Null call from user");
        Validate.notNull(command.getDestinationNumber(), "Null destination number");
        return new WebitelCallResult()
            .setStatus("OK")
            .setInfo("+OK d917a647-7378-4b02-a3e6-0c24cc7feeac\n");
    }

    @Override
    public WebitelAuthToken authenticate(WebitelLoginCommand command) {
        Validate.notNull(command.getUsername(), "Null username");
        Validate.notNull(command.getPassword(), "Null password");
        long expires = TimeMachine.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new WebitelAuthToken()
            .setKey("7db2665d-1e59-4f80-b65c-2372f32d678d")
            .setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NjQ1ODcxNzY5MTAsImFjbCI6eyJjZHIiOlsiKiJdLCJjZHIvZmlsZXMiOlsiKiJdLCJjZHIvbWVkaWEiOlsiKiJdfX0.VuQ4Ql7Yq8112E63l3vAnS_ZRzPGMdH_GWiJYh8-p_Y")
            .setExpires(expires)
            .setUsername("100@webitel.alfa.com");
    }
}
