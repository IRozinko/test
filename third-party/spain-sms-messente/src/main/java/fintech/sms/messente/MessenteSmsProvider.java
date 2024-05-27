package fintech.sms.messente;

import com.messente.sdk.Messente;
import com.messente.sdk.exception.MessenteException;
import com.messente.sdk.response.MessenteResponse;
import fintech.sms.Sms;
import fintech.sms.spi.SmsException;
import fintech.sms.spi.SmsProvider;
import fintech.sms.spi.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component(MessenteSmsProvider.NAME)
public class MessenteSmsProvider implements SmsProvider {

    public static final String NAME = "spain-messente-sms-provider";

    private final String username;
    private final String password;

    public MessenteSmsProvider(@Value("${sms.messente.username:}") String username,
                               @Value("${sms.messente.password:}") String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public SmsResponse send(Sms sms) {
        Messente messente = new Messente(username, password);

        try {
            MessenteResponse response = messente.sendSMS(sms.getSenderId(), sms.getTo(), sms.getText());
            if (response.isSuccess()) {
                log.debug("Sms sent successfully via Messente: {}", response.getResponseMessage());

                return SmsResponse.builder()
                    .id(response.getResult())
                    .providerName(NAME)
                    .message(response.getRawResponse())
                    .build();
            } else {
                log.error("Failed to send sms via Messente: {}", response.getResponseMessage());
                throw new SmsException("Failed to send sms via Messente: " + response.getResponseMessage());
            }
        } catch (MessenteException e) {
            log.error("Failed to send sms via Messente", e);
            throw new SmsException("Failed to send sms via Messente", e);
        }
    }
}
