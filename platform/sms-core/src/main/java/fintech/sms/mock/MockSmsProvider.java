package fintech.sms.mock;

import fintech.sms.Sms;
import fintech.sms.spi.SmsProvider;
import fintech.sms.spi.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component(MockSmsProvider.NAME)
public class MockSmsProvider implements SmsProvider {

    public static final String NAME = "mock-sms-provider";

    private RuntimeException nextException;

    private Sms lastSms;

    @Override
    public SmsResponse send(Sms sms) {
        this.lastSms = sms;
        log.info("Mock sms sending: {}", sms);
        if (nextException != null) {
            RuntimeException exception = nextException;
            nextException = null;
            throw exception;
        }
        return SmsResponse.builder().id(UUID.randomUUID().toString()).providerName(NAME).build();
    }

    public void failNextSms(RuntimeException exception) {
        this.nextException = exception;
    }

    public Sms getLastSms() {
        return lastSms;
    }

}
